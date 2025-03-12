package team4.train.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import team4.train.model.PlanDetail;
import team4.train.service.PlanDetailDTO;
import team4.train.service.PlanDetailService;

@RestController
@RequestMapping("/api/details")
public class PlanDetailController {

    @Autowired
    private PlanDetailService planDetailService;

    

   
// // 返回 DTO 的列表
//    @GetMapping
//    public ResponseEntity<List<PlanDetailDTO>> getAllDetailsAsDTO() {
//        List<PlanDetailDTO> details = planDetailService.getAllDetailsAsDTO();
//        return ResponseEntity.ok(details);
//    }

    // 根據 ID 查詢健身方案細則
    @GetMapping("/{id}")
    public ResponseEntity<PlanDetailDTO> getDetailWithIds(@PathVariable int id) {
        PlanDetail detail = planDetailService.getDetailWithIdsById(id);

        // 將 PlanDetail 映射為 PlanDetailDTO
        PlanDetailDTO dto = new PlanDetailDTO(
            detail.getId(),
            detail.getTrainingPlan().getId(),
            detail.getTrainingPlan().getName(),
            detail.getTrainBean().getId(),
            detail.getTrainBean().getName(), // 添加動作名稱
            detail.getSets(),
            detail.getReps(),
            detail.getCalories()
        );

        return ResponseEntity.ok(dto);
    }

    

    // 刪除健身方案細則
    @DeleteMapping("/{id}")
    public void deleteDetail(@PathVariable int id) {
        planDetailService.deleteDetail(id);
    }
    
 // 新增健身方案細則
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<PlanDetail> addPlanDetail(@RequestBody PlanDetailDTO detailDTO) {
        PlanDetail savedDetail = planDetailService.addDetailAndCalculateCalories(detailDTO);
        return ResponseEntity.ok(savedDetail);
    }
    // 更新健身方案細則
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PlanDetail> updatePlanDetail(
            @PathVariable int id,
            @RequestBody PlanDetailDTO detailDTO) {
        PlanDetail updatedDetail = planDetailService.updateDetail(id, detailDTO);
        return ResponseEntity.ok(updatedDetail);
    }
    
    
    //使用 planName 模糊查詢
    @GetMapping
    public ResponseEntity<List<PlanDetailDTO>> getDetailsByPlanName(@RequestParam(required = false) String planName) {
        List<PlanDetailDTO> details;
        if (planName != null && !planName.isEmpty()) {
            details = planDetailService.getDetailsByPlanName(planName);
        } else {
            details = planDetailService.getAllDetailsAsDTO();
        }
        return ResponseEntity.ok(details);
    }
    
}
