package team4.train.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Data
@Table(name = "TRAINING_PLANS")
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "total_calories", nullable = false)
    private int totalCalories;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "member_id")
    private Integer memberId; // Null 表示公版

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false; // 預設未假刪除

    @OneToMany(mappedBy = "trainingPlan", cascade = CascadeType.ALL)
    @JsonManagedReference("trainingPlanReference")
    private List<PlanDetail> planDetails;
}