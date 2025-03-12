package team4.train.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import team4.train.model.TrainingPlan;
import team4.train.service.TrainingPlanDTO;
import team4.train.service.TrainingPlanService;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class TrainingPlanController {

    @Autowired
    private TrainingPlanService trainingPlanService;

    // 新增方案
    @PostMapping
    public TrainingPlan addPlan(@RequestBody TrainingPlanDTO trainingPlanDTO) {
        return trainingPlanService.addOrUpdatePlan(trainingPlanDTO);
    }

    // 編輯方案
    @PutMapping("/{id}")
    public TrainingPlan updatePlan(@PathVariable int id, @RequestBody TrainingPlanDTO trainingPlanDTO) {
        trainingPlanDTO.setId(id);
        return trainingPlanService.addOrUpdatePlan(trainingPlanDTO);
    }

    // 查詢所有方案（支援會員專屬和公版）
    @GetMapping
    public Page<TrainingPlan> getAllPlans(@RequestParam(required = false) Integer memberId,
                                          @RequestParam(required = false) String name,
                                          Pageable pageable) {
        if (name != null) {
            return trainingPlanService.searchPlansByName(name, pageable);
        }
        if (memberId == null) {
            return trainingPlanService.getAllPlans(pageable);
        } else {
            return trainingPlanService.getPlansForMemberPaged(memberId, pageable);
        }
    }

    // 查詢會員專屬和公版方案（不分頁）
    @GetMapping("/member/{memberId}")
    public List<TrainingPlan> getPlansForMember(@PathVariable Integer memberId) {
        return trainingPlanService.getPlansForMember(memberId);
    }

    // 根據 ID 查詢方案
    @GetMapping("/{id}")
    public TrainingPlanDTO getPlanById(@PathVariable int id) {
        return trainingPlanService.getPlanById(id);
    }

    // 假刪除
    @DeleteMapping("/{id}")
    public void deletePlan(@PathVariable int id) {
        trainingPlanService.softDeletePlan(id);
    }
    
    //恢復假刪除
    @PutMapping("/restore/{id}")
    public void restorePlan(@PathVariable int id) {
        trainingPlanService.restorePlan(id);
    }
    
    //模糊查詢
    @GetMapping("/search")
    public Page<TrainingPlan> searchPlansByName(@RequestParam String name, Pageable pageable) {
        return trainingPlanService.searchPlansByName(name, pageable);
    }
}
