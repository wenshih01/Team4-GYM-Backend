package team4.admin.board.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationMessage {
    private Integer id;
    private String type;
    private String content;
    private String senderUsername;
    private String senderAvatar;
    private Integer postId;
    private Integer commentId;
    private LocalDateTime createdAt;
}