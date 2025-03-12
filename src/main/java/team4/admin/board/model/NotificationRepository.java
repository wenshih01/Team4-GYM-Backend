package team4.admin.board.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    
    // 獲取用戶的通知列表，按時間倒序排序
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    // 獲取用戶的未讀通知數量
    Long countByRecipientIdAndIsReadFalse(Integer userId);
    
    // 標記所有通知為已讀
    @Modifying
    @Transactional
    @Query(value = "UPDATE notifications SET is_read = 1 WHERE recipient_id = :userId AND is_read = 0", nativeQuery = true)
    void markAllAsRead(@Param("userId") Integer userId);
    
    // 標記指定通知為已讀
    @Modifying
    @Transactional
    @Query(value = "UPDATE notifications SET is_read = 1 WHERE notification_id = :notificationId", nativeQuery = true)
    void markAsRead(@Param("notificationId") Integer notificationId);
    
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.post.id = :postId OR n.comment.id IN :commentIds")
    void deleteByPostIdOrCommentIdIn(Integer postId, List<Integer> commentIds);
}