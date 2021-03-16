/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2019-08-02 19:09
 */
package online.loopcode.tailmall.service;

import online.loopcode.tailmall.model.GridCategory;
import online.loopcode.tailmall.repository.GridCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GridCategoryService {
    @Autowired
    private GridCategoryRepository gridCategoryRepository;
    public List<GridCategory> getGridCategoryList() {
        return gridCategoryRepository.findAll();
    }
}
