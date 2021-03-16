/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2020-03-24 14:35
 */
package online.loopcode.tailmall.logic;

import lombok.Getter;
import online.loopcode.tailmall.exception.http.ParameterException;
import online.loopcode.tailmall.model.OrderSku;
import online.loopcode.tailmall.model.Sku;
import online.loopcode.tailmall.model.bo.SkuOrderBO;
import online.loopcode.tailmall.model.dto.OrderDTO;
import online.loopcode.tailmall.model.dto.SkuInfoDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderChecker {

    private OrderDTO orderDTO;
    @Getter
    private List<Sku> serverSkuList;
    private CouponChecker couponChecker;
    private Integer maxSkuLimit;

    @Getter
    private List<OrderSku> orderSkuList = new ArrayList<>();

    public OrderChecker(OrderDTO orderDTO, List<Sku> serverSkuList,
                        CouponChecker couponChecker, Integer maxSkuLimit) {
        this.orderDTO = orderDTO;
        this.serverSkuList = serverSkuList;
        this.couponChecker = couponChecker;
        this.maxSkuLimit = maxSkuLimit;
    }

    public void isOK() {
        BigDecimal serverTotalPrice = new BigDecimal("0");
        List<SkuOrderBO> skuOrderBOList = new ArrayList<>();

        this.skuNotOnSale(orderDTO.getSkuInfoList().size(), this.serverSkuList.size());

        for (int i = 0; i < this.serverSkuList.size(); i++) {
            Sku serverSku = this.serverSkuList.get(i);
            SkuInfoDTO skuInfoDTO = this.orderDTO.getSkuInfoList().get(i);
            this.containsSoldOutSku(serverSku);
            this.beyondSkuStock(serverSku, skuInfoDTO);
            this.beyondMaxSkuLimit(skuInfoDTO);

            serverTotalPrice = serverTotalPrice.add(this.calculateSkuOrderPrice(serverSku, skuInfoDTO));
            skuOrderBOList.add(new SkuOrderBO(serverSku, skuInfoDTO));
            this.orderSkuList.add(new OrderSku(serverSku, skuInfoDTO));
        }

        // 前后端最终价格是否一致
        this.totalPriceIsOk(orderDTO.getTotalPrice(), serverTotalPrice);

        if (this.couponChecker != null) {
            // 如果有优惠券则需要验证最终价格
            this.couponChecker.isOk();
            this.couponChecker.canBeUsed(skuOrderBOList, serverTotalPrice);
            this.couponChecker.finalTotalPriceIsOk(skuOrderBOList, orderDTO.getFinalTotalPrice(), serverTotalPrice);
        } else {
            if (orderDTO.getFinalTotalPrice()
                    .compareTo(serverTotalPrice) != 0) {
                throw new ParameterException(50005);
            }
        }
    }

    /**
     * 前后端最终价格是否一致
     * @param orderTotalPrice 前端传来的总价格
     * @param serverTotalPrice 后端计算的总价格
     */
    private void totalPriceIsOk(BigDecimal orderTotalPrice, BigDecimal serverTotalPrice) {
        if (orderTotalPrice.compareTo(serverTotalPrice) != 0) {
            throw new ParameterException(50005);
        }
    }


    private BigDecimal calculateSkuOrderPrice(Sku serverSku, SkuInfoDTO skuInfoDTO) {
        if (skuInfoDTO.getCount() <= 0) {
            throw new ParameterException(50007);
        }
        return serverSku.getActualPrice().multiply(new BigDecimal(skuInfoDTO.getCount()));
    }

    private void skuNotOnSale(int count1, int count2) {
        if (count1 != count2) {
            throw new ParameterException(50002);
        }
    }

    private void containsSoldOutSku(Sku sku) {
        if (sku.getStock() == 0) {
            throw new ParameterException(50001);
        }
    }

    private void beyondSkuStock(Sku sku, SkuInfoDTO skuInfoDTO) {
        if (sku.getStock() < skuInfoDTO.getCount()) {
            throw new ParameterException(50003);
        }
    }

    private void beyondMaxSkuLimit(SkuInfoDTO skuInfoDTO) {
        if (skuInfoDTO.getCount() > this.maxSkuLimit) {
            throw new ParameterException(50004);
        }
    }

}