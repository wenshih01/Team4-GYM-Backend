package team4.train.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import team4.howard.member.model.UserBean;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "execution_records")
public class ExecutionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // 主鍵

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private UserBean member; // 會員（外鍵關聯 Member 表）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private TrainingPlan trainingPlan; // 執行的健身方案（外鍵關聯 TrainingPlan 表）

    @Column(name = "execution_date", nullable = false)
    private LocalDate executionDate; // 執行日期
}
