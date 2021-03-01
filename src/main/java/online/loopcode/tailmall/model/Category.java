/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2019-07-15 01:39
 */
package online.loopcode.tailmall.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Where(clause = "delete_time is null and online = 1")
public class Category extends BaseEntity {

    @Id
    private Long id;

    private String name;

    private String description;

    private Boolean isRoot;

    private String img;

    private Long parentId;

    private Long index;

    /**
     * 如果配置了双向的关系，一定要有选择地序列化，避免循环序列化问题的产生
     */
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "coupon_category",
//            joinColumns = @JoinColumn(name = "category_id"),
//            inverseJoinColumns = @JoinColumn(name = "coupon_id"))
//    private List<Coupon> couponList;

}
