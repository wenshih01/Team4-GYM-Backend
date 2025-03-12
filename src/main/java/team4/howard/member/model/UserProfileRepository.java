package team4.howard.member.model;


import org.springframework.data.jpa.repository.JpaRepository;
import team4.howard.member.model.UserProfile;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    Optional<UserProfile> findByUserId(Integer userId);
}