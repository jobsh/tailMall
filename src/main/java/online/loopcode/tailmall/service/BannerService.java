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
