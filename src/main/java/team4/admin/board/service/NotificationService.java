package team4.admin.board.service;

import lombok.RequiredArgsConstructor;
import team4.admin.board.model.Comment;
import team4.admin.board.model.Notification;
import team4.admin.board.model.NotificationMessage;
import team4.admin.board.model.NotificationRepository;
import team4.admin.board.model.NotificationType;
import team4.admin.board.model.Post;
import team4.admin.board.model.PostReportEvent;
import team4.admin.board.model.PostRepository;
import team4.howard.member.model.UserBean;
import team4.howard.member.model.UserRepository;
import team4.howard.member.model.UserService;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    /**
     * 獲取用戶的通知列表
     */
    /**
     * 獲取用戶的通知列表
     */
    public Page<Notification> getUserNotifications(Integer userId, Pageable pageable) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable);
    }


    /**
     * 獲取用戶的未讀通知數量
     */
    public Long getUnreadCount(Integer userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    /**
     * 標記通知為已讀
     */
    @Transactional
    public void markAsRead(Integer notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    /**
     * 標記用戶所有通知為已讀
     */
    @Transactional
    public void markAllAsRead(Integer userId) {
        notificationRepository.markAllAsRead(userId);
    }

    /**
     * 創建按讚通知
     */
    @Transactional
    public void createLikeNotification(Post post, UserBean sender) {
        if (post.getUser().getId() == sender.getId()) {
            return;
        }

        Notification notification = Notification.builder()
                .recipientId(post.getUser().getId())
                .senderId(sender.getId())
                .postId(post.getPostId())
                .type("like_post")
                .content(sender.getUsername() + " 對你的貼文按讚了")
                .build();

        notification = notificationRepository.save(notification);
        sendNotification(notification);
    }
    @Transactional
    public void createCommentNotification(Post post, Comment comment, UserBean sender) {
        if (post.getUser().getId() == sender.getId()) {
            return;
        }

        Notification notification = Notification.builder()
                .recipientId(post.getUser().getId())
                .senderId(sender.getId())
                .postId(post.getPostId())
                .commentId(comment.getCommentId())
                .type("comment_post")
                .content(sender.getUsername() + " 在你的貼文發表了留言")
                .build();

        notification = notificationRepository.save(notification);
        sendNotification(notification);
    }

    /**
     * 創建留言按讚通知
     */
    @Transactional
    public void createLikeCommentNotification(Comment comment, UserBean sender) {
        // 不要給自己發送通知
    	if (comment.getUser().getId() == sender.getId()) {
    	    return;
    	}

        Notification notification = Notification.builder()
                .recipientId(comment.getUser().getId())
                .senderId(sender.getId())
                .postId(comment.getPost().getPostId())
                .commentId(comment.getCommentId())
                .type(NotificationType.LIKE_COMMENT.getType())
                .content(NotificationType.LIKE_COMMENT.formatMessage(sender.getUsername()))
                .build();

        notificationRepository.save(notification);
    }
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Notification notification) {
        NotificationMessage message = NotificationMessage.builder()
            .id(notification.getId())
            .type(notification.getType())
            .content(notification.getContent())
            .senderUsername(notification.getSender().getUsername())
            .senderAvatar(notification.getSender().getAvatar())
            .postId(notification.getPostId())
            .commentId(notification.getCommentId())
            .createdAt(notification.getCreatedAt())
            .build();

        // Fetch the sender eagerly if needed
        UserBean sender = notification.getSender();
        if (sender != null) {
            message.setSenderUsername(sender.getUsername());
            message.setSenderAvatar(sender.getAvatar());
        }

        messagingTemplate.convertAndSendToUser(
            notification.getRecipientId().toString(),
            "/topic/notifications",
            message
        );
    }
    @Autowired
    private UserService userService;
    
   
    private final PostRepository postRepository; 
//    public void sendReportNotification(Integer recipientId, Integer postId, boolean isRemoved) {
//        // 獲取管理員用戶
//        UserBean admin = userService.findByUsername("admin")
//                .orElseThrow(() -> new RuntimeException("Admin user not found"));
//        Post post = postService.getPostById(postId)
//                .orElseThrow(() -> new RuntimeException("Post not found"));
//                
//        // 製作貼文內容摘要（取前 20 字）
//        String postContent = post.getContent();
//        String contentSummary = postContent.length() > 20 
//            ? postContent.substring(0, 20) + "..." 
//            : postContent;
//            
//        // 組合通知內容
//        String notificationContent = String.format(
//            "你的貼文「%s」因「%s」已被移除", 
//            contentSummary,
//            post.getReportReason()
//        );
//        
//        NotificationType type = NotificationType.POST_REPORTED;
//        
//        Notification notification = Notification.builder()
//            .recipientId(recipientId)
//            .senderId(admin.getId())  // 使用實際的管理員 ID
//            .postId(postId)
//            .type(type.getType())
//            .content(notificationContent)
//            .isRead(false)
//            .createdAt(LocalDateTime.now())
//            .build();
//
//        Notification savedNotification = notificationRepository.save(notification);
//
//        // 建立通知訊息
//        NotificationMessage message = NotificationMessage.builder()
//            .id(savedNotification.getId())
//            .type(type.getType())
//            .content(type.formatMessage(null))
//            .postId(postId)
//            .senderUsername(admin.getUsername())  // 添加發送者用戶名
//            .createdAt(savedNotification.getCreatedAt())
//            .build();
//
//        // 發送 WebSocket 通知
//        messagingTemplate.convertAndSendToUser(
//            recipientId.toString(),
//            "/topic/notifications",
//            message
//        );
//    }
    
    @EventListener
    public void handlePostReportEvent(PostReportEvent event) {
        UserBean admin = userService.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
                
        Post post = postRepository.findById(event.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        String contentSummary = post.getContent().length() > 20 
            ? post.getContent().substring(0, 20) + "..." 
            : post.getContent();
            
        String notificationContent = String.format(
            "你的貼文「%s」因「%s」已被移除", 
            contentSummary,
            post.getReportReason()
        );

        NotificationType type = NotificationType.POST_REPORTED;
        
        Notification notification = Notification.builder()
            .recipientId(event.getRecipientId())
            .senderId(admin.getId())
            .postId(event.getPostId())
            .type(type.getType())
            .content(notificationContent)
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();

        Notification savedNotification = notificationRepository.save(notification);

        NotificationMessage message = NotificationMessage.builder()
            .id(savedNotification.getId())
            .type(type.getType())
            .content(notificationContent)
            .postId(event.getPostId())
            .senderUsername(admin.getUsername())
            .createdAt(savedNotification.getCreatedAt())
            .build();

        messagingTemplate.convertAndSendToUser(
            event.getRecipientId().toString(),
            "/topic/notifications",
            message
        );
    }
}
    

    /**
     * 在其他服務中調用此方法來發送通知
     * 例如：在 PostService 的 likePost 方法中
     */
    // 在 PostService.java 中：
    /*
    @Transactional
    public void likePost(Integer postId, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // 處理按讚邏輯...
        
        // 發送通知
        notificationService.createLikeNotification(post, user);
    }
    */
