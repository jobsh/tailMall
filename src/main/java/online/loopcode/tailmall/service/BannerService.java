/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2020-02-03 17:43
 */
package online.loopcode.tailmall.service;

import online.loopcode.tailmall.model.Banner;
import online.loopcode.tailmall.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BannerService{
    @Autowired
    private BannerRepository bannerRepository;

    public Banner getByName(String name){
        return bannerRepository.findOneByName(name);
    }
}
