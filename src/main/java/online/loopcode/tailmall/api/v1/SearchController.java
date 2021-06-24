package online.loopcode.tailmall.api.v1;

import online.loopcode.tailmall.model.Spu;
import online.loopcode.tailmall.model.bo.PageCounter;
import online.loopcode.tailmall.model.vo.SpuSimplifyVO;
import online.loopcode.tailmall.model.vo.page.PagingDozer;
import online.loopcode.tailmall.service.SearchService;
import online.loopcode.tailmall.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("search")
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;
    @GetMapping("")
    public PagingDozer<Spu, SpuSimplifyVO> search(@RequestParam String q,
                                                  @RequestParam(defaultValue = "0") Integer start,
                                                  @RequestParam(defaultValue = "10") Integer count) {
        PageCounter counter = CommonUtil.convertToPageParameter(start, count);
        Page<Spu> page = this.searchService.search(q, counter.getPage(), counter.getCount());
//        if (page.getContent().isEmpty()) {
//            return
//        }
        return new PagingDozer<>(page, SpuSimplifyVO.class);
    }
}