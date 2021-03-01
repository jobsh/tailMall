/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2020-02-27 00:27
 */
package online.loopcode.tailmall.model.vo.page;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Paging<T> {
    /* 总条数 */
    private Long total;
    /* 每页条数 */
    private Integer count;
    /* 当前页码 */
    private Integer page;
    /* 总页数 */
    private Integer totalPage;
    /* 数据 */
    private List<T> items;

    public Paging(Page<T> pageT) {
        this.initPageParameters(pageT);
        this.items = pageT.getContent();
    }

    void initPageParameters(Page<T> pageT){
        this.total = pageT.getTotalElements();
        this.count = pageT.getSize();
        this.page = pageT.getNumber();
        this.totalPage = pageT.getTotalPages();
    }
}
