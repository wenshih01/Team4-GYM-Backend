package team4.shopping.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    // 根據 userId 查詢購物車
    Optional<Cart> findByUserId(int userId);
}
