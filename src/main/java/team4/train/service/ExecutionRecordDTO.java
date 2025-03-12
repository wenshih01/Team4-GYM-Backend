package team4.train.service;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ExecutionRecordDTO {
    private int id; // 執行記錄的主鍵
    private int memberId; // 會員的 ID
    private String memberName; // 會員名稱
    private int planId; // 健身方案的 ID
    private String planName; // 健身方案名稱
    private int planTotalCalories; // 健身方案的總熱量
    private LocalDate executionDate; // 執行日期
}