package online.loopcode.tailmall.repository;

import online.loopcode.tailmall.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    //    {List, List, List}
//     List, List
    List<Category> findAllByIsRootOrderByIndexAsc(Boolean isRoot);
}
