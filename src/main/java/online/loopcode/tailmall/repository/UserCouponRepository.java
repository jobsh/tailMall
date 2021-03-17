/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2019-08-19 17:28
 */
package online.loopcode.tailmall.repository;

import online.loopcode.tailmall.model.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findFirstByUserIdAndCouponIdAndStatus(Long uid, Long couponId, int status);

    Optional<UserCoupon> findFirstByUserIdAndCouponId(Long uid, Long couponId);


    @Modifying
    @Query("update UserCoupon uc\n" +
            "set uc.status = 2, uc.orderId = :oid\n" +
            "where uc.userId = :uid\n" +
            "and uc.couponId = :couponId\n" +
            "and uc.status = 1\n" +
            "and uc.orderId is null") // 这里必须要判断status 是否为1，避免在高并发的情况下，同一用户去核销同一张优惠券， orderId is null 的判断也是一种保护机制
    int writeOff(Long couponId, Long oid, Long uid);

    @Modifying
    @Query("update UserCoupon uc set uc.status=2 where uc.userId=:uid and uc.couponId=:couponId")
    int test(Long couponId, Long uid);

    @Modifying
    @Query("update UserCoupon c \n" +
            "set c.status=1, c.orderId = null \n" +
            "where c.couponId=:couponId \n" +
            "and c.userId = :uid \n" +
            "and c.orderId is not null \n" +
            "and c.status = 2")
    int returnBack(Long couponId, Long uid);
}