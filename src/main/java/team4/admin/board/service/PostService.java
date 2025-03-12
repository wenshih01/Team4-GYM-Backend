package team4.admin.board.service;

import team4.admin.board.dto.CommentDTO;
import team4.admin.board.dto.PostDTO;
import team4.admin.board.model.Comment;
import team4.admin.board.model.CommentLikeRepository;
import team4.admin.board.model.CommentRepository;
import team4.admin.board.model.NotificationRepository;
import team4.admin.board.model.Post;
import team4.admin.board.model.PostLike;
import team4.admin.board.model.PostLikeRepository;
import team4.admin.board.model.PostReportEvent;
import team4.admin.board.model.PostRepository;
import team4.howard.member.model.UserBean;
import team4.howard.member.model.UserService;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import team4.admin.board.dto.PostDTO;

@Service
@Transactional
public class PostService {
	@Autowired
	private PostRepository postRepository;

	@Autowired
	private PostLikeRepository postLikeRepository;

	@Autowired
	private UserService userService;
	
	@Autowired
	private NotificationService notificationService; 
	
	@Autowired
	private NotificationRepository notificationRepository; 
	
	@Autowired
	private CommentLikeRepository commentLikeRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	

	public List<Post> getAllPosts() {
	    List<Post> posts = postRepository.findAll();
	    posts.forEach(post -> Hibernate.initialize(post.getUser())); // 顯式初始化
	    return posts;
	}

	public Optional<Post> getPostById(int id) {
		return postRepository.findById(id);
	}

	// 修改這個方法，改用 user 關聯查詢
	public List<Post> getPostsByUserId(int userId) {
		return postRepository.findByUser_Id(userId); // 使用底線連接屬性路徑
	}

	// 這個方法不需要修改，因為我們已經在 Repository 中更新了對應的方法
	public List<Post> getPostsByUsername(String username) {
		return postRepository.findByUser_Username(username); // 確保使用底線連接
	}

	public Post savePost(Post post) {
		return postRepository.save(post);
	}
	

	public void deletePost(int postId) {
		postRepository.deleteById(postId);
	}
	 @Transactional
	    public PostDTO convertToDTO(Post post, Integer currentUserId) { // 新增 currentUserId 參數
	        try {
	            PostDTO dto = new PostDTO();
	            dto.setPostId(post.getPostId());
	            
	            // 檢查 user 是否為 null
	            if (post.getUser() != null) {
	                dto.setUserId(post.getUser().getId());
	                dto.setUsername(post.getUser().getUsername());
	                dto.setUserAvatar(post.getUser().getAvatar());
	            } else {
	                System.out.println("警告：Post ID " + post.getPostId() + " 的 user 為 null");
	                dto.setUserId(0);
	                dto.setUsername("未知用戶");
	            }
	            
	            dto.setContent(post.getContent());
	            dto.setLikesCount(post.getLikesCount());
	            dto.setCommentsCount(post.getCommentsCount());
	            dto.setReported(post.isReported());
	            dto.setCreatedAt(post.getCreatedAt());
	            dto.setUpdatedAt(post.getUpdatedAt());
	            dto.setReviewStatus(post.getReviewStatus());
	            dto.setImageUrls(post.getImageUrls());
	            dto.setReported(post.isReported());
	            dto.setReportReason(post.getReportReason());
	            dto.setReportedBy(post.getReportedBy());
	            
	            // 處理留言相關邏輯
	            if (post.getComments() != null) {
	                List<CommentDTO> parentComments = post.getComments().stream()
	                    .filter(comment -> comment.getParentComment() == null)
	                    .map(comment -> {
	                        CommentDTO commentDTO = convertCommentToDTO(comment, currentUserId);
	                        // 添加回覆
	                        commentDTO.setReplies(comment.getReplies().stream()
	                            .map(reply -> convertCommentToDTO(reply, currentUserId))
	                            .collect(Collectors.toList()));
	                        return commentDTO;
	                    })
	                    .collect(Collectors.toList());
	                dto.setComments(parentComments);
	            }

	            // 處理檢舉者資訊
	            if (post.getReportedBy() != null) {
	                Optional<UserBean> reportUser = userService.findUserById(post.getReportedBy());
	                reportUser.ifPresent(user -> dto.setReportedByUsername(user.getUsername()));
	            }

	            // 處理點讚狀態
	            if (currentUserId != null) {
	                Optional<UserBean> currentUser = userService.findUserById(currentUserId);
	                if (currentUser.isPresent()) {
	                    boolean isLiked = postLikeRepository.existsByPostAndUser(post, currentUser.get());
	                    dto.setIsLiked(isLiked);
	                }
	            }
	            
	            
	            
	            return dto;
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("轉換 Post 到 DTO 時發生錯誤：" + e.getMessage());
	            throw e;
	        }
	    }

	    // 提供一個不帶 currentUserId 的重載方法,用於向後兼容
	    @Transactional
	    public PostDTO convertToDTO(Post post) {
	        return convertToDTO(post, null);
	    }
	
	    public CommentDTO convertCommentToDTO(Comment comment, Integer currentUserId) {
	        CommentDTO dto = new CommentDTO();
	        dto.setCommentId(comment.getCommentId());
	        dto.setContent(comment.getContent());
	        dto.setUserId(comment.getUser().getId());
	        dto.setUsername(comment.getUser().getUsername());
	        dto.setUserAvatar(comment.getUser().getAvatar());
	        dto.setCreatedAt(comment.getCreatedAt());
	        dto.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null);
	        dto.setReplyLevel(comment.getReplyLevel());
	        dto.setLikesCount(comment.getLikesCount());  // 新增點讚數

	        // 檢查當前用戶是否點讚過
	        if (currentUserId != null) {
	            UserBean currentUser = userService.findUserById(currentUserId).orElse(null);
	            if (currentUser != null) {
	                boolean isLiked = commentLikeRepository.existsByCommentAndUser(comment, currentUser);
	                dto.setLiked(isLiked);
	            }
	        }

	        return dto;
	    }
	    
	    
	// 圖片處理相關的方法保持不變
	public List<String> saveImages(List<MultipartFile> images) {
		List<String> imageUrls = new ArrayList<>();

		for (MultipartFile image : images) {
			try {
				String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
				String uploadDir = "uploads/images/";
				Path uploadPath = Paths.get(uploadDir);

				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				Path filePath = uploadPath.resolve(fileName);
				Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

				String imageUrl = "/images/" + fileName;
				imageUrls.add(imageUrl);

			} catch (IOException e) {
				throw new RuntimeException("無法儲存檔案。錯誤: " + e.getMessage());
			}
		}
		return imageUrls;
	}

	public void deleteImages(List<String> imageUrls) {
		if (imageUrls == null || imageUrls.isEmpty()) {
			return;
		}

		for (String imageUrl : imageUrls) {
			try {
				// 從 URL 中獲取檔案路徑
				String filePath = "uploads/images/" + imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
				Path path = Paths.get(filePath);
				Files.deleteIfExists(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Transactional
    public Map<String, Object> toggleLike(int postId, int userId) {
        try {
            // 獲取貼文
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            // 獲取用戶
            UserBean user = userService.findUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 檢查是否已經按讚
            boolean isLiked = postLikeRepository.existsByPostAndUser(post, user);
            Map<String, Object> result = new HashMap<>();

            if (isLiked) {
                // 如果已經按讚，則取消讚
                postLikeRepository.deleteByPostAndUser(post, user);
                post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
                result.put("isLiked", false);
            } else {
                // 如果還沒按讚，則新增讚
                PostLike postLike = new PostLike();
                postLike.setPost(post);
                postLike.setUser(user);
                postLikeRepository.save(postLike);
                post.setLikesCount(post.getLikesCount() + 1);
                result.put("isLiked", true);
            }

            // 保存更新後的貼文
            post = postRepository.save(post);
            result.put("likesCount", post.getLikesCount());
            result.put("postId", post.getPostId());

            return result;
        } catch (Exception e) {
            // 記錄詳細錯誤信息
            e.printStackTrace();
            throw new RuntimeException("Error toggling like: " + e.getMessage());
        }
    }
	
	 public Page<Post> getAllVisiblePostsPaged(Pageable pageable) {
	        return postRepository.findByVisibleTrue(pageable);
	    }
	 public Page<Post> getAllPostsPaged(Pageable pageable) {
	        try {
	        	return postRepository.findAll(pageable);
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new RuntimeException("獲取貼文分頁數據失敗", e);
	        }
	    }
	 
	 // 分頁獲取用戶的貼文
	    public Page<Post> getPostsByUserIdPaged(Integer userId, Pageable pageable) {
	        return postRepository.findByUser_Id(userId, pageable);
	    }

	    // 獲取用戶的貼文總數
	    public long countPostsByUserId(Integer userId) {
	        return postRepository.countByUser_Id(userId);
	    }
	    
	    @Transactional
	    public void deletePostAndRelatedData(int postId) {
	        // 1. 先刪除相關的通知
	        notificationRepository.deleteByPostIdOrCommentIdIn(
	            postId, 
	            commentRepository.findByPostPostId(postId)
	                .stream()
	                .map(Comment::getCommentId)
	                .collect(Collectors.toList())
	        );
	        
	        // 2. 刪除貼文的點讚記錄
	        postLikeRepository.deleteByPostId(postId);
	        
	        // 3. 刪除評論的點讚記錄和評論
	        commentRepository.findByPostPostId(postId).forEach(comment -> {
	            commentLikeRepository.deleteByComment_CommentId(comment.getCommentId());
	        });
	        commentRepository.deleteByPostPostId(postId);
	        
	        // 4. 最後刪除貼文
	        postRepository.deleteById(postId);
	    }
	    
	    public Page<Post> getVisiblePostsByUserIdPaged(Integer userId, Pageable pageable) {
	        return postRepository.findVisibleByUser_Id(userId, pageable);
	    }
	    
	    private final ApplicationEventPublisher eventPublisher;
	    
	    public PostService(ApplicationEventPublisher eventPublisher) {
	        this.eventPublisher = eventPublisher;
	    }
	    
	    public void updatePost(Post post, String reviewStatus) {
	        if ("已駁回".equals(reviewStatus)) {
	            post.setVisible(false);
	            // 發布事件而不是直接調用 NotificationService
	            eventPublisher.publishEvent(new PostReportEvent(
	                post.getUser().getId(),
	                post.getPostId(),
	                true
	            ));
	        }

}
}