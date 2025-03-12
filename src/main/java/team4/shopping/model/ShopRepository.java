package team4.shopping.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShopRepository extends JpaRepository<GymBean, Integer> {

    // 根據名稱進行模糊查詢
    List<GymBean> findByNameContaining(String name);

    // 根據分類查詢商品
    List<GymBean> findByCategory(String category);

    // 根據商品狀態查詢（上架：1 或 下架：0）
    List<GymBean> findByStatus(int status);

    // 查詢特定分類且上架的商品
    List<GymBean> findByCategoryAndStatus(String category, int status);

    // 查詢價格範圍內的商品
    List<GymBean> findByPriceBetween(double minPrice, double maxPrice);

    // 查詢庫存低於指定數量的商品
    List<GymBean> findByStockQuantityLessThan(int quantity);
    
    
    @Query("SELECT DISTINCT g.category FROM GymBean g WHERE g.category IS NOT NULL")
    List<String> findAllCategories();
    
}
