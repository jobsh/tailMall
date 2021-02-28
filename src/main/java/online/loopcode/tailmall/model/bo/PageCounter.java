/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2020-02-26 23:58
 */
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
