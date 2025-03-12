package team4.admin.board.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import team4.admin.board.model.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostPostId(int postId);
    List<Comment> findByUserIdOrderByCreatedAtDesc(int userId);
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.postId = :postId")
    void deleteByPostPostId(@Param("postId") Integer postId);
}