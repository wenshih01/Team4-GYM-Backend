package team4.health.food.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Integer> {
	// 模糊搜尋 Food 名稱
    List<Food> findByFoodNameContaining(String food);
}
