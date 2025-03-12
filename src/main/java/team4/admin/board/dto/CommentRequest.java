package team4.admin.board.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private int postId;
    private int userId;
    private String content;
}