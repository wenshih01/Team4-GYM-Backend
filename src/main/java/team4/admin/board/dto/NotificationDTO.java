package team4.admin.board.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team4.admin.board.model.Notification;


//DTO 類
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
 private Integer id;
 private String type;
 private String content;
 private String senderUsername;
 private String senderAvatar;
 private Integer postId;
 private Integer commentId;
 private Boolean isRead;
 private LocalDateTime createdAt;

 // 從實體轉換為 DTO 的靜態方法
 public static NotificationDTO fromEntity(Notification notification) {
     return NotificationDTO.builder()
             .id(notification.getId())
             .type(notification.getType())
             .content(notification.getContent())
             .senderUsername(notification.getSender().getUsername())
             .senderAvatar(notification.getSender().getAvatar())
             .postId(notification.getPostId())
             .commentId(notification.getCommentId())
             .isRead(notification.getIsRead())
             .createdAt(notification.getCreatedAt())
             .build();
 }
}