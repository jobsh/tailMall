package online.loopcode.tailmall.api.v1;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import online.loopcode.tailmall.exception.http.NotFoundException;
import online.loopcode.tailmall.model.Theme;
import online.loopcode.tailmall.model.vo.ThemePureVO;
import online.loopcode.tailmall.service.ThemeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("theme")
@Validated
public class ThemeController {

    @Autowired
    private ThemeService themeService;

    @GetMapping("/by/names")

//    @ScopeLevel(8)
    public List<ThemePureVO> getThemeGroupByNames(@RequestParam(name = "names") String names) {
        List<String> nameList = Arrays.asList(names.split(","));
        List<Theme> themes = themeService.findByNames(nameList);
        List<ThemePureVO> list = new ArrayList<>();
        themes.forEach(theme -> {
            Mapper mapper = DozerBeanMapperBuilder.buildDefault();
            ThemePureVO vo = mapper.map(theme, ThemePureVO.class);
            list.add(vo);
        });
        return list;
    }

    //VIP分组 16
    //User分组 2

    @GetMapping("/name/{name}/with_spu")
    public Theme getThemeByNameWithSpu(@PathVariable(name = "name") String themeName){
        Optional<Theme> optionalTheme = this.themeService.findByName(themeName);
        return optionalTheme.orElseThrow(()-> new NotFoundException(30003));
    }
}
