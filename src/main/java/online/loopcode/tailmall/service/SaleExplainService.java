package online.loopcode.tailmall.service;

import online.loopcode.tailmall.model.SaleExplain;
import online.loopcode.tailmall.repository.SaleExplainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleExplainService {
    @Autowired
    private SaleExplainRepository saleExplainRepository;

    public List<SaleExplain> getSaleExplainFixedList() {
        return this.saleExplainRepository.findByFixedOrderByIndex(true);
    }
}
