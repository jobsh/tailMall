package online.loopcode.tailmall.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter

@Where(clause = "delete_time is null and online = 1 ")
public class Spu extends BaseEntity {
    @Id
    private Long id;
    private String title;
    private String subtitle;
    private Long categoryId;
    private Long rootCategoryId;
    private Boolean online;
    private String price;
    private Long sketchSpecId;
    private Long defaultSkuId;
    private String img;
    private String discountPrice;
    private String description;
    private String tags;
    private Boolean isTest;

//    private Object spuThemeImg;
    private String forThemeImg;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="spuId")
    private List<Sku> skuList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="spuId")
    private List<SpuImg> spuImgList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="spuId")
    private List<SpuDetailImg> spuDetailImgList;

}
