/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.coo
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2020-03-05 02:09
 */
package online.loopcode.tailmall.api.v1;

import online.loopcode.tailmall.exception.http.NotFoundException;
import online.loopcode.tailmall.model.Category;
import online.loopcode.tailmall.model.GridCategory;
import online.loopcode.tailmall.model.vo.CategoriesAllVO;
import online.loopcode.tailmall.service.CategoryService;
import online.loopcode.tailmall.service.GridCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("category")
@RestController
@ResponseBody
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private GridCategoryService gridCategoryService;

    @GetMapping("/all")
    public CategoriesAllVO getAll(){
        Map<Integer, List<Category>> categories = categoryService.getAll();
        return new CategoriesAllVO(categories);
    }

    @GetMapping("/grid/all")
    public List<GridCategory> getGridCategoryList() {
        List<GridCategory> gridCategoryList = gridCategoryService.getGridCategoryList();
        if (gridCategoryList.isEmpty()) {
            throw new NotFoundException(30009);
        }
        return gridCategoryList;
    }
}
