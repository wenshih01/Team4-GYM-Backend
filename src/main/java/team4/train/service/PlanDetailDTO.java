package team4.train.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanDetailDTO {
    private int id;                 // 方案細則 ID
    private int planId;             // 方案 ID
    private String planName; 		// 方案名稱
    private int actionId;           // 動作 ID
    private String actionName;  	// 動作名稱
    private int sets;               // 組數
    private int reps;               // 每組次數
    private int calories;           // PlanDetail總熱量消耗
}
