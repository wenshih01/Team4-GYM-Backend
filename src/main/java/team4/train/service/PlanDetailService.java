package team4.train.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import team4.train.model.PlanDetail;
import team4.train.model.PlanDetailRepository;
import team4.train.model.TrainingPlan;
import team4.train.model.TrainingPlanRepository;
import team4.train.model.TrainBean;
import team4.train.model.TrainBeanRepository;

@Service
public class PlanDetailService {

    @Autowired
    private PlanDetailRepository planDetailRepository;

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private TrainBeanRepository trainBeanRepository;

   

    
    // 返回包含 DTO 的 List
    public List<PlanDetailDTO> getAllDetailsAsDTO() {
        List<PlanDetail> details = planDetailRepository.findAll();

        return details.stream()
                .map(detail -> new PlanDetailDTO(
                        detail.getId(),
                        detail.getTrainingPlan().getId(),
                        detail.getTrainingPlan().getName(), // 方案名稱
                        detail.getTrainBean().getId(),
                        detail.getTrainBean().getName(), // 動作名稱
                        detail.getSets(),
                        detail.getReps(),
                        detail.getCalories()
                ))
                .collect(Collectors.toList());
    }

    // 根據 ID 查詢健身方案細則
    public PlanDetail getDetailWithIdsById(int id) {
        return planDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detail not found with id: " + id));
    }

    // 新增健身方案細則
    public PlanDetail addDetailAndCalculateCalories(PlanDetailDTO planDetailDTO) {
        // 查詢方案
        TrainingPlan trainingPlan = trainingPlanRepository.findById(planDetailDTO.getPlanId())
                .orElseThrow(() -> new RuntimeException("TrainingPlan not found with id: " + planDetailDTO.getPlanId()));

        // 查詢動作
        TrainBean trainBean = trainBeanRepository.findById(planDetailDTO.getActionId())
                .orElseThrow(() -> new RuntimeException("TrainBean not found with id: " + planDetailDTO.getActionId()));

        // 計算總熱量
        int totalCalories = planDetailDTO.getSets() *planDetailDTO.getReps()* trainBean.getCalories();

        // 將資料封裝為 PlanDetail
        PlanDetail planDetail = new PlanDetail();
        planDetail.setTrainingPlan(trainingPlan);
        planDetail.setTrainBean(trainBean);
        planDetail.setSets(planDetailDTO.getSets());
        planDetail.setReps(planDetailDTO.getReps());
        planDetail.setCalories(totalCalories);

        // 儲存至資料庫
        return planDetailRepository.save(planDetail);
    }

    // 更新健身方案細則
    public PlanDetail updateDetail(int id, PlanDetailDTO planDetailDTO) {
        PlanDetail existingDetail = planDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PlanDetail not found with id: " + id));

        TrainingPlan trainingPlan = trainingPlanRepository.findById(planDetailDTO.getPlanId())
                .orElseThrow(() -> new RuntimeException("TrainingPlan not found with id: " + planDetailDTO.getPlanId()));

        TrainBean trainBean = trainBeanRepository.findById(planDetailDTO.getActionId())
                .orElseThrow(() -> new RuntimeException("TrainBean not found with id: " + planDetailDTO.getActionId()));

        existingDetail.setTrainingPlan(trainingPlan);
        existingDetail.setTrainBean(trainBean);
        existingDetail.setSets(planDetailDTO.getSets());
        existingDetail.setReps(planDetailDTO.getReps());
        existingDetail.setCalories(planDetailDTO.getCalories());

        return planDetailRepository.save(existingDetail);
    }

    // 刪除健身方案細則
    public void deleteDetail(int id) {
        planDetailRepository.deleteById(id);
    }
    
    
    
    //使用 planName 模糊查詢
    public List<PlanDetailDTO> getDetailsByPlanName(String planName) {
        List<PlanDetail> details = planDetailRepository.findByTrainingPlanName(planName);

        return details.stream()
                .map(detail -> new PlanDetailDTO(
                        detail.getId(),
                        detail.getTrainingPlan().getId(),
                        detail.getTrainingPlan().getName(), // 方案名稱
                        detail.getTrainBean().getId(),
                        detail.getTrainBean().getName(), // 動作名稱
                        detail.getSets(),
                        detail.getReps(),
                        detail.getCalories()
                ))
                .collect(Collectors.toList());
    }
    
    
    //加入map進入方式
    
    public PlanDetail mapDtoToEntity(PlanDetailDTO detailDTO) {
        PlanDetail planDetail = new PlanDetail();

        // 設置 TrainBean
        TrainBean trainBean = trainBeanRepository.findById(detailDTO.getActionId())
            .orElseThrow(() -> new RuntimeException("TrainBean not found with ID: " + detailDTO.getActionId()));
        planDetail.setTrainBean(trainBean);

        // 設置 TrainingPlan
        TrainingPlan trainingPlan = trainingPlanRepository.findById(detailDTO.getPlanId())
            .orElseThrow(() -> new RuntimeException("TrainingPlan not found with ID: " + detailDTO.getPlanId()));
        planDetail.setTrainingPlan(trainingPlan);

        // 設置其他屬性
        planDetail.setSets(detailDTO.getSets());
        planDetail.setReps(detailDTO.getReps());
        planDetail.setCalories(detailDTO.getCalories());

        return planDetail;
    }

    public PlanDetail addOrUpdateDetail(PlanDetailDTO detailDTO) {
        PlanDetail planDetail = mapDtoToEntity(detailDTO);
        return planDetailRepository.save(planDetail);
    }
    
    
    


}
