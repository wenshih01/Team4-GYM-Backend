package team4.health.dailyfood.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface DailyFoodRepository extends JpaRepository<DailyFood, Integer> {

    // 查詢特定用戶的所有飲食紀錄
	 List<DailyFood> findByUserId_Id(Integer userId);

    // 查詢特定日期的飲食紀錄
	@Query("SELECT d FROM DailyFood d WHERE d.date = :date")
    List<DailyFood> findByDate(Date date);
    
    // 根據食物名稱查詢 DailyFood 記錄
    List<DailyFood> findByFoodNameContaining(String foodName);
    
    // 根據 userId 和 date 查詢 DailyFood 記錄
    List<DailyFood> findByUserId_IdAndDate(Integer userId, Date date);
    
}
