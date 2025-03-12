package team4.howard.member.model;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserBean, Integer> {

    // 根據使用者名稱查詢
    Optional<UserBean> findByUsername(String username);

    // 根據電子郵件查詢
    Optional<UserBean> findByEmail(String email);

    // 根據名稱進行模糊查詢
    List<UserBean> findByUsernameContaining(String username);
    
    Optional<UserBean> findByResetToken(String resetToken);

}
