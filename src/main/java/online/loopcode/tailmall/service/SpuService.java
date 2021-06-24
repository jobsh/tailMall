package online.loopcode.tailmall.service;

import online.loopcode.tailmall.model.Spu;
import online.loopcode.tailmall.repository.SpuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpuService {
    @Autowired
    SpuRepository spuRepository;

    public Spu getSpu(Long id) {
        return this.spuRepository.findOneById(id);
    }

    public Page<Spu> getLatestPagingSpu(Integer pageNum, Integer size) {
        Pageable page = PageRequest.of(pageNum, size, Sort.by("createTime").descending());
        return this.spuRepository.findAll(page);
    }

    public List<Spu> getByIDList(List<Long> idList){
        return this.spuRepository.findByIdIn(idList);
    }

    public Page<Spu> getByCategory(Long cid, Boolean isRoot, Integer pageNum, Integer size) {
        Pageable page = PageRequest.of(pageNum, size);
        if(isRoot){
            return this.spuRepository.findByRootCategoryIdOrderByCreateTime(cid, page);
        }
        else{
            return this.spuRepository.findByCategoryIdOrderByCreateTimeDesc(cid, page);
        }
    }
}
