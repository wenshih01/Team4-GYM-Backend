package team4.train.model;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainBeanRepository extends JpaRepository<TrainBean, Integer> {
    // 模糊查詢動作名稱
    Page<TrainBean> findByNameContaining(String name, Pageable pageable);
    

}

