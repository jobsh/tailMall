package online.loopcode.tailmall.service;

import online.loopcode.tailmall.core.enumeration.CouponStatus;
import online.loopcode.tailmall.exception.http.NotFoundException;
import online.loopcode.tailmall.exception.http.ParameterException;
import online.loopcode.tailmall.model.Activity;
import online.loopcode.tailmall.model.Coupon;
import online.loopcode.tailmall.model.UserCoupon;
import online.loopcode.tailmall.repository.ActivityRepository;
import online.loopcode.tailmall.repository.CouponRepository;
import online.loopcode.tailmall.repository.UserCouponRepository;
import online.loopcode.tailmall.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    public List<Coupon> getByCategory(Long cid) {
        Date now = new Date();
        return couponRepository.findByCategory(cid, now);
    }

    public List<Coupon> getWholeStoreCoupons() {
        Date now = new Date();
        return couponRepository.findByWholeStore(true, now);
    }

    public List<Coupon> getMyAvailableCoupons(Long uid) {
        Date now = new Date();
        return this.couponRepository.findMyAvailable(uid, now);
    }

    public List<Coupon> getMyUsedCoupons(Long uid) {
        Date now = new Date();
        return this.couponRepository.findMyUsed(uid, now);
    }

    public List<Coupon> getMyExpiredCoupons(Long uid) {
        Date now = new Date();
        return this.couponRepository.findMyExpired(uid, now);
    }

    /**
     * 用户领取优惠券
     * @param uid
     * @param couponId
     */
    public void collectOneCoupon(Long uid, Long couponId){
        // 判断是否存在这张优惠券
        this.couponRepository
                .findById(couponId)
                .orElseThrow(() -> new NotFoundException(40003));

        // 查出优惠券对应的活动
        Activity activity = this.activityRepository
                .findByCouponListId(couponId)
                .orElseThrow(() -> new NotFoundException(40010));

        // 判断活动是否过期
        Date now = new Date();
        Boolean isIn = CommonUtil.isInTimeLine(now, activity.getStartTime(), activity.getEndTime());
        if(!isIn){
            throw new ParameterException(40005);
        }

        // 判断用户是否领取过该优惠券
        this.userCouponRepository
                .findFirstByUserIdAndCouponId(uid, couponId)
                // 存在则抛出异常
                .ifPresent((uc)-> {throw new ParameterException(40006);});

        // 创建用户优惠券
        UserCoupon userCouponNew = UserCoupon.builder()
                .userId(uid)
                .couponId(couponId)
                // 默认优惠券状态为可使用
                .status(CouponStatus.AVAILABLE.getValue())
                .createTime(now)
                .build();
        // 记录用户领取的优惠券
        userCouponRepository.save(userCouponNew);
    }






















//    public List<Coupon> getMyAvailableCoupons(Long uid) {
//        Date now = new Date();
//        return couponRepository.findMyAvailable(uid, now);
//    }
//
//    public List<Coupon> getMyUsedCoupons(Long uid) {
//        Date now = new Date();
//        return couponRepository.findMyUsed(uid, now);
//    }
//
//    public List<Coupon> getMyExpiredCoupons(Long uid) {
//        Date now = new Date();
//        return couponRepository.findUserExpired(uid, now);
//    }
//

//    public void writeOff(Long couponId) {
//
//    }

//    @Transactional
//    public void collectOneCoupon(Long uid, Long couponId) {
//        Date now = new Date();
//        // 很好的Optional案例
//        this.couponRepository
//                .findById(couponId)
//                .orElseThrow(()->new NotFound(40003));
//        Optional<Activity> optionalActivity = this.activityRepository.findByCouponListId(couponId);
//        Activity activity = optionalActivity
//                .orElseThrow(()->new NotFound(40010));
//
//        // TODO：有问题，应该判断活动是否过期
//        Boolean isIn = CommonUtil.isInTimeLine(now, activity.getStartTime(), activity.getEndTime());
//        if (!isIn) {
//            throw new ParameterException(40005);
//        }
//        UserCoupon userCoupon = userCouponRepository.findFirstByUserIdAndCouponId(uid, couponId);
//        if (userCoupon != null) {
//            throw new ParameterException(40006);
//        }
//        UserCoupon userCouponNew = UserCoupon.builder()
//                .couponId(couponId)
//                .userId(uid)
//                .status(CouponStatus.AVAILABLE.value())
//                .createTime(now)
//                .build();
//        userCouponRepository.save(userCouponNew);
//    }

//    public Coupon getCoupon(Long id) {
//        return couponRepository.getOne(id);
//    }


//    public List<Coupon> getListByActivityId(Long aid) {
//        return null;
////        return couponRepository.findAllByActivityListId(aid);
//    }

}
