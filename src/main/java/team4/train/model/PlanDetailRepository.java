package team4.train.model;

import java.util.List;

//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlanDetailRepository extends JpaRepository<PlanDetail, Integer> {
    
	// 分頁查詢
//    Page<PlanDetail> findAll(Pageable pageable);

	// 在 Plandetil 頁面內使用模糊查詢
    @Query("SELECT d FROM PlanDetail d WHERE d.trainingPlan.name LIKE %:planName%")
    List<PlanDetail> findByTrainingPlanName(@Param("planName") String planName);
}
