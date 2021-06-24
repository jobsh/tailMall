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

    private final OrderDTO orderDTO;
    @Getter
    private final List<Sku> serverSkuList;
    private final CouponChecker couponChecker;
    private final Integer maxSkuLimit;

    @Getter
    private final List<OrderSku> orderSkuList = new ArrayList<>();

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

        // 前后端sku总价格是否一致
        this.totalPriceIsOk(orderDTO.getTotalPrice(), serverTotalPrice);

        // 如果使用了优惠券，则对优惠券进行校验
        if (this.couponChecker != null) {
            // 如果有优惠券则需要验证最终价格
            this.couponChecker.isOk();
            // 订单金额够不够优惠券使用资格，主要针对满减券（包括满减折扣券和满减券）
            BigDecimal orderCategoryPrice = this.couponChecker.canBeUsed(skuOrderBOList, serverTotalPrice);
            // 前后端最终价格是否一致，serverTotalPrice - 优惠券的优惠了多少钱 = serverFianalTotalPrice
            this.couponChecker.finalTotalPriceIsOk(orderDTO.getFinalTotalPrice(), serverTotalPrice, orderCategoryPrice);
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

    // 订单中每件sku的总价，用sku实际价格 * 当前sku数量
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
