package online.loopcode.tailmall.model.bo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PageCounter {
    /* 第几页 */
    private Integer page;
    /* 每页数量 size */
    private Integer count;
}
