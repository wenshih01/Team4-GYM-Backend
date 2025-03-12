package team4.admin.board.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Integer> {
    boolean existsByFollowerIdAndFollowingId(Integer followerId, Integer followingId);
    void deleteByFollowerIdAndFollowingId(Integer followerId, Integer followingId);
    long countByFollowerId(Integer followerId);
    long countByFollowingId(Integer followingId);
}