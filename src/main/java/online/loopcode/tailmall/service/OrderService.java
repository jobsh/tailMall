package online.loopcode.tailmall.service;

import lombok.Data;
import online.loopcode.tailmall.core.LocalUser;
import online.loopcode.tailmall.core.enumeration.OrderStatus;
import online.loopcode.tailmall.core.money.IMoneyDiscount;
import online.loopcode.tailmall.exception.http.ForbiddenException;
import online.loopcode.tailmall.exception.http.NotFoundException;
import online.loopcode.tailmall.exception.http.ParameterException;
import online.loopcode.tailmall.logic.CouponChecker;
import online.loopcode.tailmall.logic.OrderChecker;
import online.loopcode.tailmall.model.Coupon;
import online.loopcode.tailmall.model.Order;
import online.loopcode.tailmall.model.OrderSku;
import online.loopcode.tailmall.model.Sku;
import online.loopcode.tailmall.model.dto.OrderDTO;
import online.loopcode.tailmall.model.dto.SkuInfoDTO;
import online.loopcode.tailmall.repository.CouponRepository;
import online.loopcode.tailmall.repository.OrderRepository;
import online.loopcode.tailmall.repository.SkuRepository;
import online.loopcode.tailmall.repository.UserCouponRepository;
import online.loopcode.tailmall.util.CommonUtil;
import online.loopcode.tailmall.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
@Service
public class OrderService {

    @Autowired
    private SkuService skuService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IMoneyDiscount iMoneyDiscount;

    @Value("${missyou.order.max-sku-limit}")
    private int maxSkuLimit;

    @Value("${missyou.order.pay-time-limit}")
    private Integer payTimeLimit;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Transactional
    public Long placeOrder(Long uid, OrderDTO orderDTO, OrderChecker orderChecker) {
        // 从orderChecker获取校验后的serverSkuList
        List<Sku> serverSkuList = orderChecker.getServerSkuList();

        String orderNo = OrderUtil.makeOrderNo();
        Calendar now = Calendar.getInstance();
        Calendar now1 = (Calendar) now.clone();
        Date expiredTime = CommonUtil.addSomeSeconds(now, this.payTimeLimit).getTime();

        Order order = getOrder(uid, orderDTO, orderNo, now1, expiredTime, serverSkuList);

        order.setSnapAddress(orderDTO.getAddress());
        order.setSnapItems(orderChecker.getOrderSkuList());

        this.orderRepository.save(order);
        //reduceStock 扣减库存，用到数据库的乐观锁思想
        this.reduceStock(orderChecker);
        //核销优惠券
        Long couponId = -1L;
        if (orderDTO.getCouponId() != null) {
            this.writeOffCoupon(orderDTO.getCouponId(), order.getId(), uid);
            couponId = orderDTO.getCouponId();
        }
        //加入到延迟消息队列
        this.sendToRedis(order.getId(), uid, couponId);
        return order.getId();
    }

    private Order getOrder(Long uid, OrderDTO orderDTO, String orderNo, Calendar now1, Date expiredTime, List<Sku> serverSkuList) {
        long totalCount = this.getTotalCount(orderDTO).longValue();
        String leaderImg = this.getLeaderImg(serverSkuList);
        String leaderTitle = this.getLeaderTitle(serverSkuList);

        return Order.builder()
                .orderNo(orderNo)
                .totalPrice(orderDTO.getTotalPrice())
                .finalTotalPrice(orderDTO.getFinalTotalPrice())
                .userId(uid)
                .totalCount(totalCount)
                .snapImg(leaderImg)
                .snapTitle(leaderTitle)
                .status(OrderStatus.UNPAID.value())
                .expiredTime(expiredTime)
                .placedTime(now1.getTime())
                .build();
    }

    public OrderChecker isOk(Long uid, OrderDTO orderDTO) {
        if (orderDTO.getFinalTotalPrice().compareTo(new BigDecimal("0")) <= 0) {
            throw new ParameterException(50011);
        }

        List<Long> skuIdList = orderDTO.getSkuInfoList()
                .stream()
                .map(SkuInfoDTO::getId)
                .collect(Collectors.toList());

        List<Sku> serverSkuList = skuService.getSkuListByIds(skuIdList);

        Long couponId = orderDTO.getCouponId();
        CouponChecker couponChecker = null;
        if (couponId != null) {
            Coupon coupon = this.couponRepository.findById(couponId)
                    .orElseThrow(() -> new NotFoundException(40004));
            this.userCouponRepository.findFirstByUserIdAndCouponIdAndStatus(uid, couponId, 1)
                    .orElseThrow(() -> new NotFoundException(50006));
            couponChecker = new CouponChecker(coupon, iMoneyDiscount);
        }
        OrderChecker orderChecker = new OrderChecker(orderDTO, serverSkuList, couponChecker, this.maxSkuLimit);
        orderChecker.isOK();
        return orderChecker;
    }

    public String getLeaderImg(List<Sku> serverSkuList) {
        return serverSkuList.get(0).getImg();
    }

    public String getLeaderTitle(List<Sku> serverSkuList) {
        return serverSkuList.get(0).getTitle();
    }

    public Integer getTotalCount(OrderDTO orderDTO) {
        return orderDTO.getSkuInfoList()
                .stream()
                .map(SkuInfoDTO::getCount)
                .reduce(Integer::sum)
                .orElse(0);
    }

    private void sendToRedis(Long oid, Long uid, Long couponId) {
//        key
//        value
//        key
        String key = uid.toString() + "," + oid.toString() + "," + couponId.toString();

        try {
            stringRedisTemplate.opsForValue().set(key, "1", this.payTimeLimit, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Page<Order> getUnpaid(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Long uid = LocalUser.getUser().getId();
        Date now = new Date();
        return this.orderRepository.findByExpiredTimeGreaterThanAndStatusAndUserId(now, OrderStatus.UNPAID.value(), uid, pageable);
    }

    public Page<Order> getByStatus(Integer status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Long uid = LocalUser.getUser().getId();
        if (status == OrderStatus.All.value()) {
            return this.orderRepository.findByUserId(uid, pageable);
        }
        return this.orderRepository.findByUserIdAndStatus(uid, status, pageable);
    }

    public Optional<Order> getOrderDetail(Long oid) {
        Long uid = LocalUser.getUser().getId();
        return this.orderRepository.findFirstByUserIdAndId(uid, oid);
    }

    public void updateOrderPrepayId(Long orderId, String prePayId) {
        Optional<Order> order = this.orderRepository.findById(orderId);
        order.ifPresent(o -> {
            o.setPrepayId(prePayId);
            this.orderRepository.save(o);
        });
        order.orElseThrow(() -> new ParameterException(10007));
    }

    /**
     * 优惠券核销
     * @param couponId
     * @param oid
     * @param uid
     */
    private void writeOffCoupon(Long couponId, Long oid, Long uid) {
        int result = this.userCouponRepository.writeOff(couponId, oid, uid);
        if (result != 1) {
            throw new ForbiddenException(40012);
        }
    }

    private void reduceStock(OrderChecker orderChecker) {
        List<OrderSku> orderSkuList = orderChecker.getOrderSkuList();
        for (OrderSku orderSku : orderSkuList) {
            int result = this.skuRepository.reduceStock(orderSku.getId(), orderSku.getCount().longValue());
            if (result != 1) {
                throw new ParameterException(50003);
            }
        }
    }

}
