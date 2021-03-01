/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2019-08-02 19:07
 */
package online.loopcode.tailmall.repository;


import online.loopcode.tailmall.model.GridCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GridCategoryRepository extends JpaRepository<GridCategory, Long> {
}
