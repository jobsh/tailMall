package online.loopcode.tailmall.repository;

import online.loopcode.tailmall.model.SaleExplain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleExplainRepository extends JpaRepository<SaleExplain, Long> {
    List<SaleExplain> findByFixedOrderByIndex(Boolean fixed);
}