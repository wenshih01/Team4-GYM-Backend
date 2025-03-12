package team4.admin.board.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class CommentDTO {
    private int commentId;
    private int postId;
    private int userId;
    private String username;  // 顯示留言者名稱
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private String userAvatar; // 如果要顯示留言者頭像
    private Integer parentCommentId;
    private int replyLevel;
    private List<CommentDTO> replies; // 子留言列表
    private int likesCount;
    private boolean Liked;
}