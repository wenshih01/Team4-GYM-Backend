package team4.health.dailyfood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import team4.health.dailyfood.model.DailyFood;
import team4.health.dailyfood.model.DailyFoodRequestDTO;
import team4.health.dailyfood.service.DailyFoodService;
import team4.health.food.model.Food;
import team4.health.food.model.FoodRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/daily")
public class DailyFoodController {
	
	@Autowired
    private DailyFoodService dailyFoodService;
	

    @Autowired
    private FoodRepository foodRepository;


  

 // 新增食物記錄
    @PostMapping("/add")
    public ResponseEntity<DailyFood> addFoodToDailyRecord(
            @RequestBody FoodRecordRequest foodRecordRequest) {  // 從請求體中獲取資料
        try {
            Integer userId = foodRecordRequest.getUserId();  // 直接從請求體中取得 userId
            System.out.println("獲取的 userId: " + userId);  // 輸出 userId 以便檢查

            if (userId == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);  // 若 userId 為 null，回傳錯誤
            }

            // 調用服務層方法
            DailyFood dailyFood = dailyFoodService.addFoodToDailyRecord(
                    userId, foodRecordRequest.getFoodId(), foodRecordRequest.getQuantity(), foodRecordRequest.getDate());

            // 回傳成功狀態碼和新增的記錄
            return new ResponseEntity<>(dailyFood, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // 處理異常，回傳錯誤訊息
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // 食物記錄請求 DTO (Data Transfer Object)
    public static class FoodRecordRequest {
        private Integer userId;  // 新增 userId 屬性
        private Integer foodId;
        private double quantity;
        private Date date;

        // Getters and Setters
        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getFoodId() {
            return foodId;
        }

        public void setFoodId(Integer foodId) {
            this.foodId = foodId;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }



//根據 userId + 日期查詢食用紀錄
    @GetMapping("/bydate")
    public ResponseEntity<List<DailyFood>> findByUserIdAndDate(
        @RequestParam Integer userId, 
        @RequestParam(required = false) String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei")); // 設定台北時區

        Date parsedDate;
        if (date == null || date.isEmpty()) {
            parsedDate = new Date(); // 若未提供日期，預設為當天
        } else {
            try {
                parsedDate = sdf.parse(date);
            } catch (ParseException e) {
                return ResponseEntity.badRequest().body(Collections.emptyList()); // 日期格式錯誤返回 400
            }
        }

        List<DailyFood> foodRecords = dailyFoodService.findByUser_UserIdAndDate(userId, parsedDate);
        return ResponseEntity.ok(foodRecords);
    }



    
   // 刪除
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteDailyFood(@RequestBody Map<String, Integer> requestData) {
        Integer userId = requestData.get("userId"); // 從請求 body 取得 userId
        Integer logId = requestData.get("logId"); // 從請求 body 取得 logId

        try {
            dailyFoodService.deleteDailyFoodById(logId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 處理刪除失敗的情況
        }
    }
    
    @Data
    @NoArgsConstructor  // 確保 Spring 能夠建立物件
    public class DailyFoodResponseDTO {
        private Integer logId;
        private String foodName;
        private double quantity;
        private Integer calories;

        // 建構子
        public DailyFoodResponseDTO(Integer logId, String foodName, double quantity, Integer calories) {
            this.logId = logId;
            this.foodName = foodName;
            this.quantity = quantity;
            this.calories = calories;
        }
    }
    
    //更新食品紀錄紀錄
    @PutMapping("/update")
    public ResponseEntity<DailyFoodResponseDTO> updateDailyFood(@RequestBody DailyFoodRequestDTO request) {
        try {
            // 轉換數據並從請求中提取資料
            Integer logId = request.getLogId();
            String foodName = request.getFoodName();
            double quantity = request.getQuantity();
            int calories = request.getCalories();
            Double carbohydrate = request.getCarbohydrate();
            Double protein = request.getProtein();
            Double fat = request.getFat();
            Double sugar = request.getSugar();
            Double fiber = request.getFiber();
            Integer userId = request.getUserId();

            // 更新資料
            DailyFood updatedFood = dailyFoodService.updateDailyFood(
                logId, foodName, quantity, calories,
                carbohydrate, protein, fat, sugar, fiber, userId
            );

            // 回應 DTO
            DailyFoodResponseDTO responseDTO = new DailyFoodResponseDTO(
                updatedFood.getLogID(),
                updatedFood.getFoodName(),
                updatedFood.getQuantity(),
                updatedFood.getCalories()
            );

            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

 // 模糊查詢 Food
    @GetMapping("/search")
    public ResponseEntity<List<Food>> searchFood(@RequestParam("query") String query) {
        List<Food> foods = foodRepository.findByFoodNameContaining(query);  // 使用模糊查詢
        return ResponseEntity.ok(foods);  // 返回 JSON 格式的數據
    }

    public static class DailyFoodResponseDTOs {
        private Integer logID;
        private Integer userId;
        private String foodName;
        private double quantity;
        private Integer calories;
        private double carbohydrate;
        private double protein;
        private double fat;
        private double sugar;
        private double fiber;
        private Date date; // 加入執行日期

        // 建構子包含所有欄位
        public DailyFoodResponseDTOs(Integer logID, Integer userId, String foodName, double quantity, Integer calories,
                                     double carbohydrate, double protein, double fat, double sugar, double fiber, Date date) {
            this.logID = logID;
            this.userId = userId;
            this.foodName = foodName;
            this.quantity = quantity;
            this.calories = calories;
            this.carbohydrate = carbohydrate;
            this.protein = protein;
            this.fat = fat;
            this.sugar = sugar;
            this.fiber = fiber;
            this.date = date;
        }

        // Getters
        public Integer getLogID() { return logID; }
        public Integer getUserId() { return userId; }
        public String getFoodName() { return foodName; }
        public double getQuantity() { return quantity; }
        public Integer getCalories() { return calories; }
        public double getCarbohydrate() { return carbohydrate; }
        public double getProtein() { return protein; }
        public double getFat() { return fat; }
        public double getSugar() { return sugar; }
        public double getFiber() { return fiber; }
        public Date getDate() { return date; }
    }

    @GetMapping("/all")
    public ResponseEntity<List<DailyFoodResponseDTOs>> getAllDailyFoods() {
        List<DailyFood> dailyFoods = dailyFoodService.findAllDailyFood();

        List<DailyFoodResponseDTOs> responseDTOs = dailyFoods.stream().map(dailyFood -> 
            new DailyFoodResponseDTOs(
                dailyFood.getLogID(),
                dailyFood.getUserId().getId(),
                dailyFood.getFoodName(),
                dailyFood.getQuantity(),
                dailyFood.getCalories(),
                dailyFood.getCarbohydrate(),
                dailyFood.getProtein(),
                dailyFood.getFat(),
                dailyFood.getSugar(),
                dailyFood.getFiber(),
                dailyFood.getDate() // 加入執行日期
            )
        ).collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    
 // 查詢特定用戶的所有飲食紀錄
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getDailyFoodsByUserId(@PathVariable Integer userId) {
        List<DailyFood> dailyFoods = dailyFoodService.getDailyFoodsByUserId(userId);

        // 轉換 List<DailyFood> 為 List<Map<String, Object>>，補上 userId
        List<Map<String, Object>> responseList = dailyFoods.stream().map(food -> {
            Map<String, Object> foodMap = new HashMap<>();
            foodMap.put("logID", food.getLogID());
            foodMap.put("userId", userId);  // 這裡手動補上 userId
            foodMap.put("foodName", food.getFoodName());
            foodMap.put("quantity", food.getQuantity());
            foodMap.put("calories", food.getCalories());
            foodMap.put("carbohydrate", food.getCarbohydrate());
            foodMap.put("protein", food.getProtein());
            foodMap.put("fat", food.getFat());
            foodMap.put("sugar", food.getSugar());
            foodMap.put("fiber", food.getFiber());
            foodMap.put("date", food.getDate());
            foodMap.put("timestamp", food.getTimestamp());
            return foodMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

}
