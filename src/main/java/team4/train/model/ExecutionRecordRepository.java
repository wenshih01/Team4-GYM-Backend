package team4.train.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team4.train.model.ExecutionRecord;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExecutionRecordRepository extends JpaRepository<ExecutionRecord, Integer> {

    // 根據會員 ID 查詢執行記錄
    List<ExecutionRecord> findByMemberId(int memberId);

    // 根據方案 ID 查詢執行記錄
    List<ExecutionRecord> findByTrainingPlanId(int planId);

    // 根據會員 ID 和執行日期查詢執行記錄
    List<ExecutionRecord> findByMemberIdAndExecutionDate(int memberId, LocalDate executionDate);

    // 根據方案 ID 和執行日期查詢執行記錄
    List<ExecutionRecord> findByTrainingPlanIdAndExecutionDate(int planId, LocalDate executionDate);

    // 根據會員 ID 和方案 ID 查詢執行記錄
    List<ExecutionRecord> findByMemberIdAndTrainingPlanId(int memberId, int planId);

    // 根據會員 ID、方案 ID 和執行日期查詢執行記錄
    List<ExecutionRecord> findByMemberIdAndTrainingPlanIdAndExecutionDate(int memberId, int planId, LocalDate executionDate);
    
    // 根據會員名稱查詢
    @Query("SELECT e FROM ExecutionRecord e WHERE LOWER(e.member.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ExecutionRecord> findByMemberNameContainingIgnoreCase(@Param("name") String name);


}
