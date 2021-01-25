/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2020-02-08 05:31
 */
package online.loopcode.tailmall.model;

import javax.persistence.*;

//sql 2条sql语句 查询2次数据库
//sql 1次 join
//PHP 实体模型

@Entity
public class BannerItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String img;
    private String keyword;
    private Short type;
    private String name;

//    private Long bannerId;

    /**
     * 双向一对多
     * 一 => 关系被维护端
     * 多 => 关系维护段
     * @JoinColumn 必须打在关系维护段(多端)，一端需要在@OneToMany(mappedBy = "banner")加上mappedBy
     * @JoinColumn打在多端的原因：
     * 要根据name = "bannerId"  生成表的外键字段banner_id， 因为这个原因 private Long bannerId; 是可以不写的
     * 多端可以通过@JoinColumn 的name 值知道它属于那个banner
     *
     * 一端可以通过@OneToMany(mappedBy = "banner") 的mappedBy 定位到banner导航属性，而banner是由JoinColume的
     * 也可以知道这个bannner下面有哪些bannnerItem
     *
     *
     * 纠错：@JoinColumn这行注释掉，同时也注释掉private Long bannerId;（不然会报错）
     *      也可以在banner_item表中生成banner_id
     *
     * 问题: 不加@JoinColumn也可以生成banner_id外键，那@JoinColumn作用是什么
     *
     */
    @ManyToOne
//    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false,name = "bannerId")
    private Banner banner;
}
