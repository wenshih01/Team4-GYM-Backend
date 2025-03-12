package team4.admin.board.controller;

import team4.admin.board.dto.PostDTO;
import team4.admin.board.dto.CommentDTO;
import team4.admin.board.model.Post;
import team4.admin.board.model.PostReportEvent;
import team4.admin.board.model.Comment;
import team4.admin.board.service.PostService;
import team4.howard.member.model.UserBean;
import team4.howard.member.model.UserDTO;
import team4.howard.member.model.UserService;
import team4.admin.board.service.CommentService;
import team4.admin.board.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) Integer currentUserId) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            Sort sortObj = Sort.by(sortDirection, sort);
            Pageable pageable = PageRequest.of(page, size, sortObj);

            Page<Post> postPage = postService.getAllPostsPaged(pageable);
            
            List<PostDTO> postDTOs = postPage.getContent().stream()
                .map(post -> {
                    try {
                        return postService.convertToDTO(post, currentUserId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", postDTOs);
            response.put("totalPages", postPage.getTotalPages());
            response.put("totalElements", postPage.getTotalElements());
            response.put("currentPage", postPage.getNumber());
            response.put("size", postPage.getSize());
            response.put("hasNext", postPage.hasNext());
                
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/feed")  // 新增一個專門給 PostFeed 用的端點
    public ResponseEntity<Map<String, Object>> getVisiblePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) Integer currentUserId) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            Sort sortObj = Sort.by(sortDirection, sort);
            Pageable pageable = PageRequest.of(page, size, sortObj);

            // 只取得非駁回的貼文
            Page<Post> postPage = postService.getAllVisiblePostsPaged(pageable);
            
            List<PostDTO> postDTOs = postPage.getContent().stream()
                .map(post -> {
                    try {
                        return postService.convertToDTO(post, currentUserId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", postDTOs);
            response.put("totalPages", postPage.getTotalPages());
            response.put("totalElements", postPage.getTotalElements());
            response.put("currentPage", postPage.getNumber());
            response.put("size", postPage.getSize());
            response.put("hasNext", postPage.hasNext());
                
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(
            @PathVariable int id,
            @RequestParam(required = false) Integer currentUserId) {
        Optional<Post> optionalPost = postService.getPostById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            List<Comment> comments = commentService.getCommentsByPostId(id);
            post.setComments(comments);
            PostDTO postDTO = postService.convertToDTO(post, currentUserId);  // 傳入 currentUserId
            return ResponseEntity.ok(postDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/id/{userId}") 
    public ResponseEntity<List<PostDTO>> getPostsByUserId(@PathVariable int userId) {
        try {
            List<Post> posts = postService.getPostsByUserId(userId);
            posts.forEach(post -> {
                List<Comment> comments = commentService.getCommentsByPostId(post.getPostId());
                post.setComments(comments);
            });

            List<PostDTO> postDTOs = posts.stream()
                    .map(postService::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(postDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<PostDTO> createPost(
            @RequestParam("username") String username,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "reviewStatus", required = false) String reviewStatus
    ) {
        try {
            Optional<UserBean> userOptional = userService.findByUsername(username);
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            Post post = new Post();
            post.setUser(userOptional.get());
            post.setContent(content);
            post.setCreatedAt(new Date());
            post.setUpdatedAt(new Date());
            post.setLikesCount(0);
            post.setCommentsCount(0);
            post.setReported(false);
            post.setReviewStatus(reviewStatus != null ? reviewStatus : "正常");

            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = postService.saveImages(images);
                post.setImageUrls(imageUrls);
            }

            Post savedPost = postService.savePost(post);
            PostDTO postDTO = postService.convertToDTO(savedPost);
            return ResponseEntity.ok(postDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable int id,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "reviewStatus", required = false) String reviewStatus,
            @RequestParam(value = "deletedImages", required = false) List<String> deletedImages
    ) {
        try {
            return postService.getPostById(id).map(existingPost -> {
                String oldStatus = existingPost.getReviewStatus();
                
                // 更新內容
                if (content != null) {
                    existingPost.setContent(content);
                }
                
                // 更新審核狀態
                if (reviewStatus != null) {
                    existingPost.setReviewStatus(reviewStatus);
                    
                    // 如果狀態改為"已駁回"且與原狀態不同，發送通知並隱藏貼文
                    if ("已駁回".equals(reviewStatus) && !reviewStatus.equals(oldStatus)) {
                        existingPost.setVisible(false);
                        
                        // 使用 PostReportEvent 發送通知
                        PostReportEvent reportEvent = new PostReportEvent(
                            existingPost.getUser().getId(),
                            existingPost.getPostId(),
                            true
                        );
                        eventPublisher.publishEvent(reportEvent);
                    }
                }

                // 處理刪除的圖片
                if (deletedImages != null && !deletedImages.isEmpty()) {
                    List<String> currentImages = existingPost.getImageUrls();
                    if (currentImages != null) {
                        currentImages.removeAll(deletedImages);
                        existingPost.setImageUrls(currentImages);
                    }
                }

                // 處理新增的圖片
                if (images != null && !images.isEmpty()) {
                    List<String> imageUrls = postService.saveImages(images);
                    if (existingPost.getImageUrls() == null) {
                        existingPost.setImageUrls(new ArrayList<>());
                    }
                    existingPost.getImageUrls().addAll(imageUrls);
                }

                existingPost.setUpdatedAt(new Date());
                Post updatedPost = postService.savePost(existingPost);
                return ResponseEntity.ok(postService.convertToDTO(updatedPost));
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable int id) {
        try {
            postService.deletePostAndRelatedData(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("處理請求時發生錯誤: " + e.getMessage());
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likePost(
            @PathVariable int id,
            @RequestParam(required = false) Boolean unlike,
            @RequestParam Integer userId) {
        try {
            Map<String, Object> result = postService.toggleLike(id, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/{id}/report")
    public ResponseEntity<Map<String, Object>> reportPost(
            @PathVariable int id,
            @RequestParam String reason,
            @RequestParam Integer reportedBy) {
        try {
            Optional<Post> postOptional = postService.getPostById(id);
            if (postOptional.isPresent()) {
                Post post = postOptional.get();
                post.setReported(true);
                post.setReportReason(reason);
                post.setReportedBy(reportedBy);
                // 新增: 設定審核狀態為待審核
                post.setReviewStatus("待審核");
                
                Post updatedPost = postService.savePost(post);
                
                Map<String, Object> response = new HashMap<>();
                response.put("postId", updatedPost.getPostId());
                response.put("reported", updatedPost.isReported());
                response.put("reviewStatus", updatedPost.getReviewStatus());
                
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user/name/{username}") 
    public ResponseEntity<Map<String, Object>> getPostsByUsername(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) Integer currentUserId) {
        try {
            // 先查找用戶
            Optional<UserBean> userOptional = userService.findByUsername(username);
            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // 設置排序和分頁
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            Sort sortObj = Sort.by(sortDirection, sort);
            Pageable pageable = PageRequest.of(page, size, sortObj);

            // 獲取分頁後的貼文，排除已駁回的貼文
            Page<Post> postPage = postService.getVisiblePostsByUserIdPaged(
                userOptional.get().getId(), 
                pageable
            );
            
            // 轉換為 DTO
            List<PostDTO> postDTOs = postPage.getContent().stream()
                .map(post -> {
                    try {
                        return postService.convertToDTO(post, currentUserId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

            // 構建回應
            Map<String, Object> response = new HashMap<>();
            response.put("content", postDTOs);
            response.put("totalPages", postPage.getTotalPages());
            response.put("totalElements", postPage.getTotalElements());
            response.put("currentPage", postPage.getNumber());
            response.put("size", postPage.getSize());
            response.put("hasNext", postPage.hasNext());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/profile/{username}")  // 修改這個路徑
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable String username) {
        try {
            Optional<UserBean> userOptional = userService.findByUsername(username);
            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            UserBean user = userOptional.get();
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("username", user.getUsername());
            profile.put("name", user.getName());
            profile.put("email", user.getEmail());
            profile.put("avatar", user.getAvatar());

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 獲取用戶統計資料的端點
    @GetMapping("/user/{username}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String username) {
        try {
            Optional<UserBean> userOptional = userService.findByUsername(username);
            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            UserBean user = userOptional.get();
            Map<String, Object> stats = new HashMap<>();

            // 獲取貼文數量
            long postsCount = postService.countPostsByUserId(user.getId());
            
            // 獲取關注者數量
            long followersCount = userService.getFollowersCount(user.getId());
            
            // 獲取正在關注的數量
            long followingCount = userService.getFollowingCount(user.getId());

            stats.put("posts", postsCount);
            stats.put("followers", followersCount);
            stats.put("following", followingCount);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    // 更新用戶資料的端點
//    @PutMapping("/user/{username}/profile")
//    public ResponseEntity<UserDTO> updateUserProfile(
//            @PathVariable String username,
//            @RequestParam(required = false) String email,
//            @RequestParam(required = false) String bio,
//            @RequestParam(required = false) MultipartFile avatar) {
//        try {
//            Optional<UserBean> userOptional = userService.findByUsername(username);
//            if (userOptional.isEmpty()) {
//                return ResponseEntity.notFound().build();
//            }
//
//            UserBean user = userOptional.get();   
//            
//            // 更新簡介
//            if (bio != null) {
//                user.setBio(bio);
//            }
//
//            // 處理頭像上傳
//            if (avatar != null && !avatar.isEmpty()) {
//                String avatarUrl = userService.saveAvatar(avatar);
//                user.setAvatar(avatarUrl);
//            }
//
//            // 保存更新
//            UserBean updatedUser = userService.updateUser(user);
//            UserDTO userDTO = userService.convertToDTO(updatedUser);
//
//            return ResponseEntity.ok(userDTO);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
    
    @GetMapping("/search")
    public ResponseEntity<List<PostDTO>> searchPosts(@RequestParam String username) {
        try {
            Optional<UserBean> userOptional = userService.findByUsername(username);
            if (userOptional.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>()); // 如果找不到用戶，返回空列表
            }

            List<Post> posts = postService.getPostsByUserId(userOptional.get().getId());
            List<PostDTO> postDTOs = posts.stream()
                    .map(post -> {
                        List<Comment> comments = commentService.getCommentsByPostId(post.getPostId());
                        post.setComments(comments);
                        return postService.convertToDTO(post);
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(postDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 檢查追蹤狀態的端點
    @GetMapping("/user/{userId}/following/{targetId}")
    public ResponseEntity<Map<String, Object>> checkFollowStatus(
            @PathVariable Integer userId,
            @PathVariable Integer targetId) {
        try {
            boolean isFollowing = userService.isFollowing(userId, targetId);
            Map<String, Object> response = new HashMap<>();
            response.put("isFollowing", isFollowing);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 追蹤/取消追蹤的端點
    @PostMapping("/user/{targetId}/follow")
    public ResponseEntity<Map<String, Object>> toggleFollow(
            @PathVariable Integer targetId,
            @RequestParam Integer userId) {
        try {
            Map<String, Object> result = userService.toggleFollow(userId, targetId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

   
}