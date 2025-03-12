package team4.admin.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team4.admin.board.dto.CommentDTO;
import team4.admin.board.model.Comment;
import team4.admin.board.model.CommentLike;
import team4.admin.board.model.CommentLikeRepository;
import team4.admin.board.model.Post;
import team4.howard.member.model.UserBean;
import team4.admin.board.model.CommentRepository;
import team4.admin.board.model.PostRepository;
import team4.howard.member.model.UserRepository;
import team4.howard.member.model.UserService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CommentLikeRepository commentLikeRepository;


    public List<Comment> getCommentsByPostId(int postId) {
        return commentRepository.findByPostPostId(postId);
    }

    public Optional<Comment> getCommentById(int id) {
        return commentRepository.findById(id);
    }

    @Transactional
    public Comment createComment(int postId, int userId, String content) {
        try {
            Comment comment = new Comment();
            
            // 獲取貼文
            Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
            
            // 設置貼文並增加評論數
            comment.setPost(post);
            post.setCommentsCount(post.getCommentsCount() + 1);
            
            // 設置其他評論資訊
            comment.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
            comment.setContent(content);
            comment.setCreatedAt(new Date());
            comment.setUpdatedAt(new Date());
            
            // 保存評論
            comment = commentRepository.save(comment);
            
            // 保存更新後的貼文
            postRepository.save(post);
            
            return comment;
        } catch (Exception e) {
            throw new RuntimeException("Error creating comment: " + e.getMessage());
        }
    }
    public Optional<Comment> updateComment(int id, String content) {
        return commentRepository.findById(id)
                .map(comment -> {
                    comment.setContent(content);
                    comment.setUpdatedAt(new Date());
                    return commentRepository.save(comment);
                });
    }

    public void deleteComment(int id) {
        commentRepository.deleteById(id);
    }

    public List<Comment> getCommentsByUserId(int userId) {
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public Comment createReply(int parentCommentId, int postId, int userId, String content) {
        Comment parentComment = commentRepository.findById(parentCommentId)
            .orElseThrow(() -> new RuntimeException("父留言不存在"));
            
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("貼文不存在"));
            
        UserBean user = userService.findUserById(userId)
            .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Comment reply = new Comment();
        reply.setParentComment(parentComment);
        reply.setReplyLevel(parentComment.getReplyLevel() + 1);
        reply.setPost(post);
        reply.setUser(user);
        reply.setContent(content);
        
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);
        
        return commentRepository.save(reply);
    }
    
    @Transactional
    public Map<String, Object> toggleLike(int commentId, int userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        UserBean user = userService.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isLiked = commentLikeRepository.existsByCommentAndUser(comment, user);
        Map<String, Object> result = new HashMap<>();

        if (isLiked) {
            // 如果已經按讚，則取消讚
            commentLikeRepository.deleteByCommentAndUser(comment, user);
            comment.setLikesCount(Math.max(0, comment.getLikesCount() - 1));
            result.put("isLiked", false);
        } else {
            // 如果還沒按讚，則新增讚
            CommentLike commentLike = new CommentLike();
            commentLike.setComment(comment);
            commentLike.setUser(user);
            commentLikeRepository.save(commentLike);
            comment.setLikesCount(comment.getLikesCount() + 1);
            result.put("isLiked", true);
        }

        comment = commentRepository.save(comment);
        result.put("likesCount", comment.getLikesCount());
        result.put("commentId", comment.getCommentId());

        return result;
    }
    
    
    public CommentDTO convertToDTO(Comment comment, Integer currentUserId) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentId(comment.getCommentId());
        dto.setPostId(comment.getPost().getPostId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setUserAvatar(comment.getUser().getAvatar());
        dto.setParentCommentId(comment.getParentComment() != null ? 
            comment.getParentComment().getCommentId() : null);
        dto.setReplyLevel(comment.getReplyLevel());
        dto.setLikesCount(comment.getLikesCount());

        // 檢查當前用戶是否按讚
        if (currentUserId != null) {
            UserBean currentUser = userService.findUserById(currentUserId)
                .orElse(null);
            if (currentUser != null) {
                dto.setLiked(commentLikeRepository.existsByCommentAndUser(comment, currentUser));
            }
        }

        // 設置回覆
        if (comment.getReplies() != null) {
            dto.setReplies(comment.getReplies().stream()
                .map(reply -> convertToDTO(reply, currentUserId))
                .collect(Collectors.toList()));
        }

        return dto;
    }
}