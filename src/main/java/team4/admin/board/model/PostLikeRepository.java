package team4.admin.board.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import team4.howard.member.model.UserBean;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    boolean existsByPostAndUser(Post post, UserBean user);
    Optional<PostLike> findByPostAndUser(Post post, UserBean user);
    void deleteByPostAndUser(Post post, UserBean user);
    @Modifying
    @Query("DELETE FROM PostLike pl WHERE pl.post.id = :postId")
    void deleteByPostId(Integer postId);
}