package team4.train.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team4.train.model.ExecutionRecord;
import team4.howard.member.model.UserBean;
import team4.howard.member.model.UserRepository;
import team4.train.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExecutionRecordService {

	@Autowired
    private ExecutionRecordRepository executionRecordRepository;
    
    // 需要用到查詢會員、方案的 Repository，請自行注入
    @Autowired
    private UserRepository userBeanRepository;

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;
    // 新增執行記錄
    public ExecutionRecord saveExecutionRecord(ExecutionRecord record) {
        return executionRecordRepository.save(record);
    }

    // 查詢所有執行記錄並轉為 DTO
    public List<ExecutionRecordDTO> getAllExecutionRecords() {
        return executionRecordRepository.findAll().stream()
                .map(record -> new ExecutionRecordDTO(
                        record.getId(),
                        record.getMember() != null ? record.getMember().getId() : -1,
                        record.getMember() != null ? record.getMember().getName() : "未知會員",
                        record.getTrainingPlan() != null ? record.getTrainingPlan().getId() : -1,
                        record.getTrainingPlan() != null ? record.getTrainingPlan().getName() : "未知方案",
                        record.getTrainingPlan() != null ? record.getTrainingPlan().getTotalCalories() : 0,
                        record.getExecutionDate()
                ))
                .collect(Collectors.toList());
    }

    // 根據 ID 查詢單一執行記錄
    public Optional<ExecutionRecordDTO> getExecutionRecordById(int id) {
        return executionRecordRepository.findById(id)
                .map(record -> new ExecutionRecordDTO(
                        record.getId(),
                        record.getMember() != null ? record.getMember().getId() : -1,
                        record.getMember() != null ? record.getMember().getName() : "未知會員",
                        record.getTrainingPlan() != null ? record.getTrainingPlan().getId() : -1,
                        record.getTrainingPlan() != null ? record.getTrainingPlan().getName() : "未知方案",
                        record.getTrainingPlan() != null ? record.getTrainingPlan().getTotalCalories() : 0,
                        record.getExecutionDate()
                ));
    }

    // 模糊查詢會員名稱
    public List<ExecutionRecordDTO> searchExecutionRecordsByMemberName(String memberName) {
        return executionRecordRepository.findAll().stream()
                .filter(record -> record.getMember() != null &&
                        record.getMember().getName().contains(memberName))
                .map(record -> new ExecutionRecordDTO(
                        record.getId(),
                        record.getMember().getId(),
                        record.getMember().getName(),
                        record.getTrainingPlan() != null ? record.getTrainingPlan().getId() : -1,
                        record.getTrainingPlan() != null ? record.getTrainingPlan().getName() : "未知方案",
                        record.getTrainingPlan() != null ? record.getTrainingPlan().getTotalCalories() : 0,
                        record.getExecutionDate()
                ))
                .collect(Collectors.toList());
    }

    // 根據 memberId 查詢執行記錄
    public List<ExecutionRecordDTO> getExecutionRecordsByMemberId(int memberId) {
        return executionRecordRepository.findAll().stream()
                .filter(record -> record.getMember() != null && record.getMember().getId() == memberId)
                .map(record -> new ExecutionRecordDTO(
                        record.getId(),
                        record.getMember().getId(),
                        record.getMember().getName(),
                        record.getTrainingPlan() != null ? record.getTrainingPlan().getId() : -1,
                        record.getTrainingPlan() != null ? record.getTrainingPlan().getName() : "未知方案",
                        record.getTrainingPlan() != null ? record.getTrainingPlan().getTotalCalories() : 0,
                        record.getExecutionDate()
                ))
                .collect(Collectors.toList());
    }
    
 // 刪除執行記錄
    public void deleteExecutionRecord(int id) {
        executionRecordRepository.deleteById(id);
    }
    
    @Transactional
    public ExecutionRecordDTO createExecutionRecord(ExecutionRecordDTO recordDto) {
        // 1. 查詢會員
        UserBean user = userBeanRepository.findById(recordDto.getMemberId())
                .orElseThrow(() -> new RuntimeException("找不到該會員 ID：" + recordDto.getMemberId()));

        // 2. 查詢健身方案
        TrainingPlan plan = trainingPlanRepository.findById(recordDto.getPlanId())
                .orElseThrow(() -> new RuntimeException("找不到該方案 ID：" + recordDto.getPlanId()));

        // 3. 建立新的 ExecutionRecord
        ExecutionRecord newRecord = new ExecutionRecord();
        newRecord.setMember(user);
        newRecord.setTrainingPlan(plan);
        newRecord.setExecutionDate(recordDto.getExecutionDate()); // 例如 2025-02-03

        // 4. 存進資料庫
        ExecutionRecord savedRecord = executionRecordRepository.save(newRecord);

        // 5. 回傳 DTO（你可視需求更動欄位）
        return new ExecutionRecordDTO(
                savedRecord.getId(),
                user.getId(),
                user.getName(),
                plan.getId(),
                plan.getName(),
                plan.getTotalCalories(),
                savedRecord.getExecutionDate()
        );
    }
}
