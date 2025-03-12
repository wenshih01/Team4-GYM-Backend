package team4.health.dailyfood.model;

import jakarta.persistence.*;
import team4.health.food.model.Food;
import team4.howard.member.model.UserBean; // 引用 UserBean

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "DailyFood")
public class DailyFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logID;  // 主鍵

    // userId 指向 UserBean 中的 id
    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    @JsonIgnore
    private UserBean userId;  // 與 UserBean 關聯

    // 將 foodid 改為 food，並且引用 Food 類別
    @ManyToOne
    @JoinColumn(name = "foodId", referencedColumnName = "foodid")
    @JsonIgnore
    private Food foodId;  // 與 Food 關聯

    @Column(name = "FoodName")
    private String foodName;

    @Column(name = "quantity")
    private double quantity;

    @Column(name = "calories")
    private Integer calories;

    @Column(name = "carbohydrate")
    private double carbohydrate;

    @Column(name = "protein")
    private double protein;

    @Column(name = "fat")
    private double fat;

    @Column(name = "sugar")
    private double sugar;

    @Column(name = "fiber")
    private double fiber;

    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")  // 格式化日期顯示為 yyyy-MM-dd
    private Date date;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, name = "timestamp")
    private Date timestamp;

    // 無參數建構子
    public DailyFood() {
    }

    // Getters 和 Setters

    public Integer getLogID() {
        return logID;
    }

    public void setLogID(Integer logID) {
        this.logID = logID;
    }

    public UserBean getUserId() {
        return userId;
    }

    public void setUserId(UserBean userId) {
        this.userId = userId;
    }

    public Food getFoodId() {
        return foodId;
    }

    public void setFoodId(Food foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public double getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(double carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public double getFiber() {
        return fiber;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
