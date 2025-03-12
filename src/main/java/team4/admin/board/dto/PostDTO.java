package team4.admin.board.dto;


import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class PostDTO {
    private int postId;
    private int userId;
    private String username;
    private String content;
    private int likesCount;
    private int commentsCount;
    private boolean reported;
    private Date createdAt;
    private Date updatedAt;
    private String reviewStatus;
    private List<String> imageUrls;
    private List<CommentDTO> comments;
    private String userAvatar;
    private boolean liked;
    private String reportReason;
    private Integer reportedBy;
    private String reportedByUsername;
    private boolean isLiked;
    
    public PostDTO() {}
    
    public boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }
}