package online.loopcode.tailmall.model.vo;

import lombok.Getter;
import lombok.Setter;
import online.loopcode.tailmall.model.Category;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class CategoryPureVO {
    private Long id;

    private String name;

    private Boolean isRoot;

    private String img;

    private Long parentId;

    private Long index;

    public CategoryPureVO(Category category) {
        BeanUtils.copyProperties(category, this);
    }
}
