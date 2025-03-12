package team4.admin.board.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import team4.howard.member.model.UserBean;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {
    boolean existsByCommentAndUser(Comment comment, UserBean user);
    void deleteByCommentAndUser(Comment comment, UserBean user);
    @Modifying
    @Query("DELETE FROM CommentLike cl WHERE cl.comment.commentId = :commentId")
    void deleteByComment_CommentId(@Param("commentId") Integer commentId);

}