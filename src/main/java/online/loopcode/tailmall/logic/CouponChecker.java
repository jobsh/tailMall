package online.loopcode.tailmall.logic;


import online.loopcode.tailmall.core.enumeration.CouponType;
import online.loopcode.tailmall.core.money.IMoneyDiscount;
import online.loopcode.tailmall.exception.http.ForbiddenException;
import online.loopcode.tailmall.exception.http.ParameterException;
import online.loopcode.tailmall.model.Category;
import online.loopcode.tailmall.model.Coupon;
import online.loopcode.tailmall.model.bo.SkuOrderBO;
import online.loopcode.tailmall.util.CommonUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CouponChecker {

    private Coupon coupon;

    private IMoneyDiscount iMoneyDiscount;

    public CouponChecker(Coupon coupon,IMoneyDiscount iMoneyDiscount) {
        this.coupon = coupon;
        this.iMoneyDiscount = iMoneyDiscount;
    }

    public void isOk() {
        Date now = new Date();
        Boolean isInTimeline = CommonUtil.isInTimeLine(now, this.coupon.getStartTime(), this.coupon.getEndTime());
        if (!isInTimeline) {
            throw new ForbiddenException(40007);
        }
    }

    //Mark
    public void finalTotalPriceIsOk(BigDecimal orderFinalTotalPrice,
                                    BigDecimal serverTotalPrice, BigDecimal orderCategoryPrice) {
        BigDecimal serverFinalTotalPrice;

        switch (CouponType.toType(this.coupon.getType())) {
            case FULL_MINUS:
            case NO_THRESHOLD_MINUS:
                // 由于已经判断了，优惠券的使用条件，这里直接减优惠金额即可
                serverFinalTotalPrice = serverTotalPrice.subtract(this.coupon.getMinus());
                if (serverFinalTotalPrice.compareTo(new BigDecimal("0")) <= 0) {
                    throw new ForbiddenException(50008);
                }
                break;
            case FULL_OFF:
//                BigDecimal orderCategoryPrice = this.canBeUsed(skuOrderBOList, serverTotalPrice);
                BigDecimal discountPrice = this.iMoneyDiscount.discount(orderCategoryPrice,  new BigDecimal(1).subtract(this.coupon.getRate()));
                serverFinalTotalPrice = serverTotalPrice.subtract(discountPrice);
                break;
            default:
                throw new ParameterException(40009);
        }
        int compare = serverFinalTotalPrice.compareTo(orderFinalTotalPrice);
        if (compare != 0) {
            throw new ForbiddenException(50008);
        }
    }

    public BigDecimal canBeUsed(List<SkuOrderBO> skuOrderBOList, BigDecimal serverTotalPrice) {
        // 订单中满足优惠条件的金额
        BigDecimal orderCategoryPrice;

        if(this.coupon.getWholeStore()){
            orderCategoryPrice = serverTotalPrice;
        }
        else{
            List<Long> cidList = this.coupon.getCategoryList()
                    .stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
            orderCategoryPrice = this.getSumByCategoryList(skuOrderBOList, cidList);
        }
        this.couponCanBeUsed(orderCategoryPrice);
        return orderCategoryPrice;
    }

    /**
     * 主要用来判断满减券和满减折扣券的是否达到使用金额条件
     * @param orderCategoryPrice
     */
    private void couponCanBeUsed(BigDecimal orderCategoryPrice) {
        switch (CouponType.toType(this.coupon.getType())){
            case FULL_OFF:
            case FULL_MINUS:
                // 不够满减条件
                int compare = this.coupon.getFullMoney().compareTo(orderCategoryPrice);
                if(compare > 0){
                    throw new ParameterException(40008);
                }
                break;
            case NO_THRESHOLD_MINUS:
                break;
            default:
                throw new ParameterException(40009);
        }
    }

    private BigDecimal getSumByCategoryList(List<SkuOrderBO> skuOrderBOList, List<Long> cidList) {
        BigDecimal sum = cidList.stream()
                .map(cid -> this.getSumByCategory(skuOrderBOList, cid))
                .reduce(BigDecimal::add)
                .orElse(new BigDecimal("0"));
        return sum;
    }

    /**
     *
     * @param skuOrderBOList
     * @param cid coupon可使用的某一个cid
     * @return
     */
    private BigDecimal getSumByCategory(List<SkuOrderBO> skuOrderBOList, Long cid) {
        BigDecimal sum = skuOrderBOList.stream()
                .filter(sku -> sku.getCategoryId().equals(cid))
                .map(SkuOrderBO::getTotalPrice)
                .reduce(BigDecimal::add)
                .orElse(new BigDecimal("0"));
        return sum;
    }
}


