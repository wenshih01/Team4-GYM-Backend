package team4.train.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class TrainingPlanDTO {
    private int id;
    private String name;
    private String description;
    private int totalCalories;
    private boolean isPublic;
    private boolean isDeleted; // 增加 isDeleted
    private Integer memberId;
    private List<PlanDetailDTO> planDetails;
}