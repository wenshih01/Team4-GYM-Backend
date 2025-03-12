package team4.health.food.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import team4.health.food.model.Food;
import team4.health.food.service.FoodService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    @Autowired
    private FoodService foodService;

 // 新增食物 (POST)
    @PostMapping
    public ResponseEntity<Food> saveFood(@RequestBody Food foodBean) {
        Food savedFood = foodService.saveFood(foodBean); // 呼叫服務層來新增食物
        return ResponseEntity.ok(savedFood);
    }

    // 更新食物 (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFood(@PathVariable int id, @RequestBody Food foodBean) {
        foodBean.setFoodId(id); // 確保 ID 一致
        Food updatedFood = foodService.updateFood(foodBean); // 呼叫服務層來更新食物
        return ResponseEntity.ok(updatedFood);
    }

    // 根據 ID 查詢食物
    @GetMapping("/{id}")
    public ResponseEntity<Food> findById(@PathVariable int id) {
        Optional<Food> food = foodService.findById(id);
        return food.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    // 查詢所有食物
    @GetMapping
    public ResponseEntity<List<Food>> findAll() {
        List<Food> foods = foodService.findAll();
        return ResponseEntity.ok(foods);
    }

    // 刪除食物
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        foodService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}