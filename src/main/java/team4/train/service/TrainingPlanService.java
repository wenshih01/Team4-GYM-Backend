package team4.train.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team4.train.model.PlanDetail;
import team4.train.model.PlanDetailRepository;
import team4.train.model.TrainBean;
import team4.train.model.TrainBeanRepository;
import team4.train.model.TrainingPlan;
import team4.train.model.TrainingPlanRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingPlanService {

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private TrainBeanRepository trainBeanRepository;
    @Autowired
    private PlanDetailRepository planDetailRepository;
    
    
    @Transactional
    public TrainingPlan addOrUpdatePlan(TrainingPlanDTO planDTO) {
        // 獲取現有方案，或創建新方案
        TrainingPlan trainingPlan = trainingPlanRepository.findById(planDTO.getId())
            .orElse(new TrainingPlan());

        trainingPlan.setId(planDTO.getId());
        trainingPlan.setName(planDTO.getName());
        trainingPlan.setDescription(planDTO.getDescription());
        trainingPlan.setPublic(planDTO.isPublic());
        trainingPlan.setMemberId(planDTO.isPublic() ? null : planDTO.getMemberId());

        // 初始化 planDetails 為空集合，避免 NullPointerException
        if (trainingPlan.getPlanDetails() == null) {
            trainingPlan.setPlanDetails(new ArrayList<>());
        }

        // 1. 獲取現有細項
        List<PlanDetail> existingDetails = trainingPlan.getPlanDetails();

        // 2. 新的細項列表
        List<PlanDetail> updatedDetails = planDTO.getPlanDetails().stream()
            .map(planDetailDTO -> {
                PlanDetail detail = new PlanDetail();
                detail.setId(planDetailDTO.getId());
                detail.setSets(planDetailDTO.getSets());
                detail.setReps(planDetailDTO.getReps());

                TrainBean trainBean = trainBeanRepository.findById(planDetailDTO.getActionId())
                    .orElseThrow(() -> new RuntimeException("TrainBean not found with ID: " + planDetailDTO.getActionId()));
                detail.setTrainBean(trainBean);

                int calories = planDetailDTO.getSets() * planDetailDTO.getReps() * trainBean.getCalories();
                detail.setCalories(calories);

                detail.setTrainingPlan(trainingPlan);
                return detail;
            })
            .collect(Collectors.toList());

        // 3. 找出需要刪除的細項
        List<PlanDetail> detailsToRemove = existingDetails.stream()
            .filter(existingDetail -> updatedDetails.stream()
                .noneMatch(updatedDetail -> updatedDetail.getId() == existingDetail.getId()))
            .collect(Collectors.toList());

        // 4. 從資料庫中刪除需要移除的細項
        if (!detailsToRemove.isEmpty()) {
            detailsToRemove.forEach(detail -> {
                planDetailRepository.deleteById(detail.getId()); // 確保資料庫同步刪除
            });
        }

        // 5. 更新方案的細項列表
        trainingPlan.getPlanDetails().clear(); // 清空現有細項列表
        trainingPlan.getPlanDetails().addAll(updatedDetails); // 添加新的細項列表

        // 6. 計算總卡路里
        int totalCalories = updatedDetails.stream()
            .mapToInt(PlanDetail::getCalories)
            .sum();
        trainingPlan.setTotalCalories(totalCalories);

        // 7. 儲存方案和細項
        return trainingPlanRepository.save(trainingPlan);
    }






    // 查詢所有方案（分頁）
    public Page<TrainingPlan> getAllPlans(Pageable pageable) {
        return trainingPlanRepository.findAll(pageable);
    }

    // 查詢會員專屬和公版方案
    public List<TrainingPlan> getPlansForMember(Integer memberId) {
        return trainingPlanRepository.findPlansForMember(memberId);
    }

    // 查詢會員專屬和公版方案（分頁）
    public Page<TrainingPlan> getPlansForMemberPaged(Integer memberId, Pageable pageable) {
        return trainingPlanRepository.findPlansForMemberPaged(memberId, pageable);
    }

    // 根據 ID 查詢方案
    public TrainingPlanDTO getPlanById(int id) {
        TrainingPlan trainingPlan = trainingPlanRepository.findById(id).orElse(null);
        if (trainingPlan == null) {
            return null;
        }

        List<PlanDetailDTO> detailDTOs = trainingPlan.getPlanDetails().stream()
            .map(detail -> new PlanDetailDTO(
                detail.getId(),
                trainingPlan.getId(),
                trainingPlan.getName(),
                detail.getTrainBean().getId(),
                detail.getTrainBean().getName(),
                detail.getSets(),
                detail.getReps(),
                detail.getCalories()
            ))
            .collect(Collectors.toList());

        return new TrainingPlanDTO(
            trainingPlan.getId(),
            trainingPlan.getName(),
            trainingPlan.getDescription(),
            trainingPlan.getTotalCalories(),
            trainingPlan.isPublic(),
            trainingPlan.isDeleted(),
            trainingPlan.getMemberId(),
            detailDTOs
        );
    }

    // 假刪除方案
    @Transactional
    public void softDeletePlan(int id) {
        TrainingPlan trainingPlan = trainingPlanRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan not found"));

        // 將 isDeleted 設為 true，表示假刪除
        trainingPlan.setDeleted(true);
        trainingPlanRepository.save(trainingPlan);
    }
    //恢復假刪除方法
    @Transactional
    public void restorePlan(int id) {
        TrainingPlan trainingPlan = trainingPlanRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan not found"));

        // 將 isDeleted 設為 false，恢復為有效方案
        trainingPlan.setDeleted(false);
        trainingPlanRepository.save(trainingPlan);
    }
    
 // 模糊查詢方案名稱
    public Page<TrainingPlan> searchPlansByName(String name, Pageable pageable) {
        return trainingPlanRepository.findByNameContaining(name, pageable);
    }

}
