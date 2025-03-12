package team4.health.dailyfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team4.health.dailyfood.model.DailyFood;
import team4.health.dailyfood.model.DailyFoodRepository;
import team4.health.food.model.Food;
import team4.health.food.model.FoodRepository;
import team4.howard.member.model.UserBean;  // 更新成 UserBean
import team4.howard.member.model.UserRepository;  // 使用 UserRepository 查詢 UserBean

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DailyFoodService {

    @Autowired
    private DailyFoodRepository dailyFoodRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserRepository userRepository;  // 用來查詢 UserBean
    
    public DailyFoodService(DailyFoodRepository dailyFoodRepository, 
            				FoodRepository foodRepository, 
            				UserRepository userRepository) {  // 更新建構子
    		this.dailyFoodRepository = dailyFoodRepository;
    		this.foodRepository = foodRepository;
    		this.userRepository = userRepository;
    	}
 
    // 根據 FoodID 查詢 Food，並新增到 DailyFood 記錄
    @Transactional
    public DailyFood addFoodToDailyRecord(Integer userId, Integer foodId, double quantity, Date date) {
        // 1. 根據 foodId 查詢 Food
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("❌ 食物 ID " + foodId + " 不存在"));

        // 2. 根據 userId 查詢 UserBean
        UserBean userBean = userRepository.findById(userId) // 轉換 userId 為整數
                .orElseThrow(() -> new RuntimeException("❌ 使用者 ID " + userId + " 不存在"));

        // 3. 創建新的 DailyFood 記錄
        DailyFood dailyFood = new DailyFood(); 
        dailyFood.setUserId(userBean); // 設置使用者
        dailyFood.setFoodId(food); // 設置食物
        dailyFood.setFoodName(food.getFoodName()); // 設置食物名稱
        dailyFood.setQuantity(quantity);  // 設置數量
        dailyFood.setCalories(food.getCalories());  // 設置卡路里
        dailyFood.setCarbohydrate(food.getCarbohydrate());  // 設置碳水化合物
        dailyFood.setProtein(food.getProtein());  // 設置蛋白質
        dailyFood.setFat(food.getFat());  // 設置脂肪
        dailyFood.setSugar(food.getSugar());  // 設置糖分
        dailyFood.setFiber(food.getFiber());  // 設置纖維
        dailyFood.setDate(date);  // 設置日期
        dailyFood.setTimestamp(new Date());  // 當前時間戳記

        // 4. 存入資料庫
        dailyFoodRepository.save(dailyFood);
        
        // 5. 回傳 DailyFood 記錄
        return dailyFood;
    }

    // 根據日期和userId查詢
    public List<DailyFood> findByUser_UserIdAndDate(Integer userId, Date date) {
        return dailyFoodRepository.findByUserId_IdAndDate(userId, date);  // 將 userId 轉為整數
    }

	 // 刪除食品紀錄
	    public void deleteDailyFoodById(Integer logId, Integer userId) {
	        // 根據 logId 查詢 DailyFood 記錄
	        DailyFood dailyFood = dailyFoodRepository.findById(logId)
	                .orElseThrow(() -> new RuntimeException("❌ 該食物紀錄 ID " + logId + " 不存在"));
	        
	        // 確認該食物紀錄是否屬於當前使用者
	        if (!Integer.valueOf(dailyFood.getUserId().getId()).equals(userId)) {
	            throw new RuntimeException("❌ 該食物紀錄不屬於當前使用者");
	        }

	        
	        // 刪除該食物紀錄
	        dailyFoodRepository.deleteById(logId);
	    }

	    // 更改食品紀錄
	    public DailyFood updateDailyFood(Integer logId, String foodName, double quantity, int calories,
	                                      Double carbohydrate, Double protein, Double fat, Double sugar, Double fiber,
	                                      Integer userId) {
	        try {
	            // 取得現有的 DailyFood 物件
	            Optional<DailyFood> optionalFood = dailyFoodRepository.findById(logId);
	            if (optionalFood.isEmpty()) {
	                throw new RuntimeException("找不到 logId: " + logId);
	            }

	            DailyFood food = optionalFood.get();

	            // 更新欄位（僅當有新值時才更新）
	            if (foodName != null) {
	                food.setFoodName(foodName);
	            }
	            if (quantity > 0) {
	                food.setQuantity(quantity);
	            }
	            if (calories > 0) {
	                food.setCalories(calories);
	            }
	            if (carbohydrate != null) {
	                food.setCarbohydrate(carbohydrate);
	            }
	            if (protein != null) {
	                food.setProtein(protein);
	            }
	            if (fat != null) {
	                food.setFat(fat);
	            }
	            if (sugar != null) {
	                food.setSugar(sugar);
	            }
	            if (fiber != null) {
	                food.setFiber(fiber);
	            }

	            // 更新 UserId
	            Optional<UserBean> userBeanOptional = userRepository.findById(userId);
	            if (userBeanOptional.isEmpty()) {
	                throw new RuntimeException("找不到 userId: " + userId);
	            }

	            food.setUserId(userBeanOptional.get()); // 設定 UserBean

	            // 保存更新後的食物資料
	            return dailyFoodRepository.save(food);
	        } catch (Exception e) {
	            throw new RuntimeException("更新失敗: " + e.getMessage());
	        }
	    }
	    
	 // 查詢所有 DailyFood 記錄
	    public List<DailyFood> findAllDailyFood() {
	        return dailyFoodRepository.findAll(); // 使用 repository 的 findAll 方法查詢所有資料
	    }
	    
	 // 查詢指定 userId 的所有 DailyFood 紀錄
	    public List<DailyFood> getDailyFoodsByUserId(Integer userId) {
	        return dailyFoodRepository.findByUserId_Id(userId);
	    }
}
