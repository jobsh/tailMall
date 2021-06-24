package online.loopcode.tailmall.api.v1;
import online.loopcode.tailmall.exception.http.NotFoundException;
import online.loopcode.tailmall.model.Spu;
import online.loopcode.tailmall.model.bo.PageCounter;
import online.loopcode.tailmall.model.vo.SpuSimplifyVO;
import online.loopcode.tailmall.model.vo.page.PagingDozer;
import online.loopcode.tailmall.service.SpuService;
import online.loopcode.tailmall.util.CommonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/spu")
@Validated
public class SpuController {
    @Autowired
    private SpuService spuService;

    @GetMapping("/id/{id}/detail")
    public Spu getDetail(@PathVariable @Positive Long id) {
        Spu spu = this.spuService.getSpu(id);
        if (spu == null) {
            throw new NotFoundException(30003);
        }
        return spu;
    }

    @GetMapping("/id/{id}/simplify")
    public SpuSimplifyVO getSimplifySpu(@PathVariable @Positive(message = "{id.positive}") Long id) {
        Spu spu = this.spuService.getSpu(id);
        SpuSimplifyVO vo = new SpuSimplifyVO();
        BeanUtils.copyProperties(spu, vo);
        return vo;
    }

    @GetMapping("/latest")
    public PagingDozer<Spu, SpuSimplifyVO> getLatestSpuList(@RequestParam(name="start",defaultValue = "0") Integer start,
                                                            @RequestParam(name="count",defaultValue = "10") Integer count) {
        PageCounter pageCounter = CommonUtil.convertToPageParameter(start, count);
        Page<Spu> page = this.spuService.getLatestPagingSpu(pageCounter.getPage(), pageCounter.getCount());
        return new PagingDozer<>(page, SpuSimplifyVO.class);
    }

    /**
     * 根据categoryId获取spuList
     * @param id 根据cateGoryId获取spuList
     * @param isRoot 是否是（根菜单）一级菜单
     * @return spuList
     */
    @GetMapping("/by/category/{id}")
    public PagingDozer<Spu, SpuSimplifyVO> getByCategoryId(@PathVariable @Positive Long id,
                                                           @RequestParam(name = "is_root", defaultValue = "false") Boolean isRoot,

                                                           @RequestParam(name = "start", defaultValue = "0")
                                                                   Integer start,
                                                           @RequestParam(name = "count", defaultValue = "10")
                                                                   Integer count) {
        PageCounter pageCounter = CommonUtil.convertToPageParameter(start, count);
        Page<Spu> page = this.spuService.getByCategory(id, isRoot, pageCounter.getPage(), pageCounter.getCount());
        return new PagingDozer<>(page, SpuSimplifyVO.class);
    }
}

