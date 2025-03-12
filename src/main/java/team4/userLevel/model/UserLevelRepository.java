	package team4.userLevel.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLevelRepository extends JpaRepository<UserLevel, Integer> {

    // 透過 userId 查詢該用戶的會員等級
    Optional<UserLevel> findByUserId(Integer userId);
}
