package online.loopcode.tailmall.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Where(clause = "delete_time is null")
public class Theme extends BaseEntity {
    @Id
    private Long id;
    private String title;
    private String description;
    private String name;
    private String extend;
    private String entranceImg;
    private String internalTopImg;
    private Boolean online;
    private String titleImg;
    /* 模板名称 */
    private String tplName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="theme_spu", joinColumns = @JoinColumn(name="theme_id")
            , inverseJoinColumns = @JoinColumn(name="spu_id"))
    private List<Spu> spuList;
}
