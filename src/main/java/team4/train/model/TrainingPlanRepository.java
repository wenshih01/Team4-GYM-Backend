package team4.train.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Integer> {
    // 模糊查詢方案名稱
	Page<TrainingPlan> findByNameContaining(String name, Pageable pageable);
    
    @Query("SELECT p FROM TrainingPlan p WHERE (p.isPublic = true OR p.memberId = :memberId) AND p.isDeleted = false")
    List<TrainingPlan> findPlansForMember(@Param("memberId") Integer memberId);

    @Query("SELECT p FROM TrainingPlan p WHERE (p.isPublic = true OR p.memberId = :memberId) AND p.isDeleted = false")
    Page<TrainingPlan> findPlansForMemberPaged(@Param("memberId") Integer memberId, Pageable pageable);

    
    @Query("SELECT p FROM TrainingPlan p WHERE p.isDeleted = true")
    List<TrainingPlan> findDeletedPlans();
}
