package team4.admin.board.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team4.howard.member.model.UserBean;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer id;  // 修改這裡，讓 getId() 方法返回這個欄位


    @Column(name = "recipient_id", nullable = false)
    private Integer recipientId;  // 接收通知的用戶ID

    @Column(name = "sender_id", nullable = false)
    private Integer senderId;     // 觸發通知的用戶ID

    @Column(name = "post_id")
    private Integer postId;       // 相關貼文ID（可為空）

    @Column(name = "comment_id")
    private Integer commentId;    // 相關留言ID（可為空）

    @Column(name = "type", nullable = false, length = 50)
    private String type;         // 通知類型 (like_post/comment_post/like_comment)

    @Column(name = "content", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;      // 通知內容

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;  // 是否已讀

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt; // 創建時間

    // 與相關實體的關聯關係
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserBean recipient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserBean sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "PostID", insertable = false, updatable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "CommentID", insertable = false, updatable = false)
    private Comment comment;
}



