
package team4.howard.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team4.howard.member.model.*;
import org.springframework.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserProfileRepository userProfileRepository;

  

    @PostMapping("/save")
    public ResponseEntity<?> saveUser(@ModelAttribute UserBean user,
                                    @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            String imageUrl = "";
            
            if (image != null && !image.isEmpty()) {
                String uploadDir = "uploads/images";
                Path uploadPath = Paths.get(uploadDir);
                
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                String originalFilename = StringUtils.cleanPath(image.getOriginalFilename());
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + fileExtension;
                
                Path filePath = uploadPath.resolve(newFileName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                imageUrl = "/uploads/images/" + newFileName;
                System.out.println("圖片已儲存到: " + filePath.toAbsolutePath());
                System.out.println("設定的 imageUrl: " + imageUrl);
                
                // 直接設定 avatar
                user.setAvatar(imageUrl);
            }
            
            // 在儲存前印出使用者物件
            System.out.println("準備儲存的使用者資料: " + user);
            
            UserBean savedUser = userService.saveUser(user);
            
            // 儲存後再次確認
            System.out.println("儲存後的使用者資料: " + savedUser);
            
            return ResponseEntity.ok(savedUser);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("儲存失敗: " + e.getMessage());
        }
    }
    
    // 刪除使用者
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Integer id) {
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
            return ResponseEntity.ok("使用者 ID " + id + " 已成功刪除！");
        }
        return ResponseEntity.status(404).body("使用者刪除失敗！");
    }

	 // 查詢單筆使用者
	    @GetMapping("/{id}")
	    public ResponseEntity<?> findUserById(@PathVariable Integer id) {
	        Optional<UserBean> user = userService.findUserById(id);
	        if (user.isPresent()) {
	            return ResponseEntity.ok(user.get());
	        }
	        return ResponseEntity.status(404).body("使用者未找到");
	    }


    // 查詢全部使用者
    @GetMapping("/list")
    public ResponseEntity<?> findAllUsers() {
        List<UserBean> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    // 更新使用者
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
            @ModelAttribute UserBean user,
            @RequestPart(value = "image_url", required = false) MultipartFile image) {
        try {
            // 處理圖片（如果提供了新圖片）
            if (image != null && !image.isEmpty()) {
                String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                File uploadDir = new File("C:/temp/upload");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                File dest = new File(uploadDir, filename);
                image.transferTo(dest);
                user.setAvatar("/users/uploads/" + filename);
            }
            // 更新使用者
            UserBean updatedUser = userService.updateUser(user);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.status(404).body("使用者不存在，更新失敗！");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("更新使用者失敗：" + e.getMessage());
        }
    }
    
	    
    @PutMapping("/updateNp")
    public ResponseEntity<?> updateUserNp(
            @ModelAttribute UserBean user,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            String imageUrl = user.getAvatar(); // 預設為現有 avatar

            if (image != null && !image.isEmpty()) {
                String uploadDir = "uploads/images";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFilename = StringUtils.cleanPath(image.getOriginalFilename());
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + fileExtension;

                Path filePath = uploadPath.resolve(newFileName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                imageUrl = "/uploads/images/" + newFileName;
                System.out.println("圖片已儲存到: " + filePath.toAbsolutePath());
                System.out.println("設定的 imageUrl: " + imageUrl);
                
                user.setAvatar(imageUrl); // 更新 avatar
            }

            // 在更新前印出使用者物件
            System.out.println("準備更新的使用者資料: " + user);

            UserBean updatedUser = userService.updateUserWithoutPassword(user);
            if (updatedUser != null) {
                System.out.println("更新後的使用者資料: " + updatedUser);
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("使用者不存在，更新失敗！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("更新使用者失敗：" + e.getMessage());
        }
    }
	    
		    // 模糊查詢
		    @GetMapping("/search")
		    public ResponseEntity<?> findUsersByName(@RequestParam(value = "name", required = false) String name) {
		        List<UserBean> users;
		
		        if (name == null || name.trim().isEmpty()) {
		            users = userService.findAllUsers();
		        } else {
		            users = userService.findUsersByName(name);
		        }
		
		        if (users.isEmpty()) {
		            return ResponseEntity.status(404).body("未找到匹配的用戶！");
		        }
		
		        return ResponseEntity.ok(users);
		    }
		    
		    // 產生重設密碼的 Token 並寄送 Email
//		    @PostMapping("/forgot-password")
//		    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
//		    	System.out.print(email);
//		        boolean success = userService.generateResetToken(email);
//		        if (success) {
//		            return ResponseEntity.ok("重設密碼連結已發送至您的 Email！");
//		        }
//		        return ResponseEntity.status(404).body("此 Email 尚未註冊！");
//		    }
		    
		    @PostMapping("/forgot-password")
		    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> requestBody) {
		        String email = requestBody.get("email");

		        // ✅ 檢查 email 是否成功接收
		        System.out.println("🔹 [forgotPassword] 收到的 email: " + email);

		        if (email == null || email.isEmpty()) {
		            System.out.println("❌ [forgotPassword] Email 為空！");
		            return ResponseEntity.badRequest().body("請提供有效的 Email！");
		        }

		        boolean success = userService.generateResetToken(email);
		        
		        if (success) {
		            System.out.println("✅ [forgotPassword] 成功發送密碼重置信件！");
		            return ResponseEntity.ok("重設密碼連結已發送至您的 Email！");
		        } else {
		            System.out.println("❌ [forgotPassword] 此 Email 尚未註冊：" + email);
		            return ResponseEntity.status(404).body("此 Email 尚未註冊！");
		        }
		    }


		    // 透過 Token 重設密碼
		    @PostMapping("/reset-password")
		    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
		        // ✅ 檢查接收到的參數
		        System.out.println("🔹 [resetPassword] 收到 Token: " + token);
		        System.out.println("🔹 [resetPassword] 收到新密碼: " + newPassword);

		        boolean success = userService.resetPassword(token, newPassword);

		        if (success) {
		            System.out.println("✅ [resetPassword] 密碼更新成功！");
		            return ResponseEntity.ok("密碼已成功更新！");
		        } else {
		            System.out.println("❌ [resetPassword] 失敗！無效或過期的 Token：" + token);
		            return ResponseEntity.status(400).body("無效或過期的 Token！");
		        }
		    }

		    @PutMapping("/{userId}/profile")
		    public ResponseEntity<?> updateUserProfile(
		            @PathVariable Integer userId,
		            @RequestParam(required = false) MultipartFile avatar,
		            @RequestParam(required = false) String bio) {
		        try {
		            // 1. 處理頭像上傳
		            UserBean user = userService.findUserById(userId)
		                .orElseThrow(() -> new RuntimeException("用戶不存在"));

		            if (avatar != null && !avatar.isEmpty()) {
		                String imageUrl = handleAvatarUpload(avatar);
		                user.setAvatar(imageUrl);
		                userService.updateUserWithoutPassword(user);
		            }

		            // 2. 處理個人簡介
		            UserProfile profile = userProfileRepository.findByUserId(userId)
		                .orElse(new UserProfile());
		            
		            if (profile.getId() == null) {
		                profile.setUser(user);
		            }
		            
		            if (bio != null) {
		                profile.setBio(bio);
		            }

		            userProfileRepository.save(profile);

		            // 3. 回傳更新後的資料
		            Map<String, Object> response = new HashMap<>();
		            response.put("avatar", user.getAvatar());
		            response.put("bio", profile.getBio());

		            return ResponseEntity.ok(response);
		            
		        } catch (Exception e) {
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("更新失敗: " + e.getMessage());
		        }
		    }

		    private String handleAvatarUpload(MultipartFile avatar) throws IOException {
		        String uploadDir = "uploads/images";
		        Path uploadPath = Paths.get(uploadDir);
		        
		        if (!Files.exists(uploadPath)) {
		            Files.createDirectories(uploadPath);
		        }
		        
		        String originalFilename = StringUtils.cleanPath(avatar.getOriginalFilename());
		        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
		        String newFileName = UUID.randomUUID().toString() + fileExtension;
		        
		        Path filePath = uploadPath.resolve(newFileName);
		        Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		        
		        return "/uploads/images/" + newFileName;
		    }

		    @GetMapping("/{userId}/profile")
		    public ResponseEntity<?> getUserProfile(@PathVariable Integer userId) {
		        try {
		            UserProfile profile = userProfileRepository.findByUserId(userId)
		                .orElseThrow(() -> new RuntimeException("找不到用戶資料"));

		            Map<String, Object> response = new HashMap<>();
		            response.put("bio", profile.getBio());
		            response.put("avatar", profile.getUser().getAvatar());

		            return ResponseEntity.ok(response);
		        } catch (Exception e) {
		            return ResponseEntity.status(HttpStatus.NOT_FOUND)
		                .body("查詢失敗: " + e.getMessage());
		        }
		    }
}