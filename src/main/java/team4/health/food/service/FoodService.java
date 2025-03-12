package team4.health.food.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team4.health.food.model.Food;
import team4.health.food.model.FoodRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    // 新增食物
    public Food saveFood(Food food) {
        return foodRepository.save(food);
    }

 // 更新食物
    public Food updateFood(Food food) {
        if (foodRepository.existsById(food.getFoodId())) {
            return foodRepository.save(food);
        } else {
            throw new NoSuchElementException("Food not found with ID " + food.getFoodId());
        }
    }

    // 根據 ID 查詢食物
    public Optional<Food> findById(int id) {
        return foodRepository.findById(id);
    }

    // 查詢所有食物
    public List<Food> findAll() {
        return foodRepository.findAll();
    }

    // 刪除食物
    public void deleteById(int id) {
        foodRepository.deleteById(id);
    }
}