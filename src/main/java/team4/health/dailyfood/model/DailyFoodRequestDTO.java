package team4.health.dailyfood.model;

import lombok.Data;

@Data
public class DailyFoodRequestDTO {
    private Integer logId;
    private String foodName;
    private double quantity;
    private Integer calories;
    private Double carbohydrate;
    private Double protein;
    private Double fat;
    private Double sugar;
    private Double fiber;
    private Integer userId;
}
