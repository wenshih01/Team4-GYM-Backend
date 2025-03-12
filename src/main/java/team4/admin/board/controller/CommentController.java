package team4.admin.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team4.admin.board.model.Comment;
import team4.admin.board.model.Post;
import team4.admin.board.service.CommentService;
import team4.admin.board.dto.CommentDTO;
import team4.admin.board.dto.CommentRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {
	@Autowired
	private CommentService commentService;

	// 轉換 Comment 到 CommentDTO
	  private CommentDTO convertToDTO(Comment comment) {
	        return commentService.convertToDTO(comment, null);
	    }

	// 獲取特定貼文的所有留言
	@GetMapping("/post/{postId}")
	public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable int postId) {
		List<Comment> comments = commentService.getCommentsByPostId(postId);
		List<CommentDTO> commentDTOs = comments.stream().map(this::convertToDTO).collect(Collectors.toList());
		return ResponseEntity.ok(commentDTOs);
	}

	// 新增留言
	@PostMapping
	public ResponseEntity<?> createComment(@RequestBody CommentRequest request) {
		try {
			// 創建評論
			Comment comment = commentService.createComment(request.getPostId(), request.getUserId(),
					request.getContent());

			// 獲取更新後的貼文資訊
			Post post = comment.getPost();

			// 創建回應對象
			Map<String, Object> response = new HashMap<>();
			response.put("comment", convertToDTO(comment));
			response.put("postId", post.getPostId());
			response.put("commentsCount", post.getCommentsCount());

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	// 更新留言
	@PutMapping("/{id}")
	public ResponseEntity<CommentDTO> updateComment(@PathVariable int id, @RequestBody CommentRequest request) {
		try {
			return commentService.updateComment(id, request.getContent())
					.map(comment -> ResponseEntity.ok(convertToDTO(comment))).orElse(ResponseEntity.notFound().build());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	// 刪除留言
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteComment(@PathVariable int id) {
		try {
			commentService.deleteComment(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	// 獲取單個留言
	@GetMapping("/{id}")
	public ResponseEntity<CommentDTO> getCommentById(@PathVariable int id) {
		return commentService.getCommentById(id).map(comment -> ResponseEntity.ok(convertToDTO(comment)))
				.orElse(ResponseEntity.notFound().build());
	}

	// 獲取特定使用者的所有留言
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<CommentDTO>> getCommentsByUserId(@PathVariable int userId) {
		List<Comment> comments = commentService.getCommentsByUserId(userId);
		List<CommentDTO> commentDTOs = comments.stream().map(this::convertToDTO).collect(Collectors.toList());
		return ResponseEntity.ok(commentDTOs);
	}

	@PostMapping("/{commentId}/reply")
	public ResponseEntity<?> replyToComment(@PathVariable int commentId, @RequestBody CommentRequest request) {
		try {
			Comment reply = commentService.createReply(commentId, request.getPostId(), request.getUserId(),
					request.getContent());

			Map<String, Object> response = new HashMap<>();
			response.put("comment", convertToDTO(reply));
			response.put("postId", reply.getPost().getPostId());
			response.put("commentsCount", reply.getPost().getCommentsCount());

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
	
	 @PostMapping("/{commentId}/like")
	    public ResponseEntity<Map<String, Object>> likeComment(
	            @PathVariable int commentId,
	            @RequestParam Integer userId) {
	        try {
	            Map<String, Object> result = commentService.toggleLike(commentId, userId);
	            return ResponseEntity.ok(result);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.internalServerError().build();
	        }
	    }
}