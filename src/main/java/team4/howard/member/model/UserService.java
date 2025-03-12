package team4.howard.member.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import io.github.cdimascio.dotenv.Dotenv;
import team4.admin.board.model.UserFollow;
import team4.admin.board.model.UserFollowRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserFollowRepository userFollowRepository;
    
  

    // 新增使用者
    public UserBean saveUser(UserBean user) {
        // 如果是更新使用者，且前端未提供密碼，則保留原密碼
    

        return userRepo.save(user);
    
    }

    // 刪除使用者
    public boolean deleteUserById(Integer id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return true;
        }
        return false;
    }

    // 查詢單筆使用者
    public Optional<UserBean> findUserById(Integer id) {
        return userRepo.findById(id);
    }

    // 查詢所有使用者
    public List<UserBean> findAllUsers() {
        return userRepo.findAll();
    }
    
    // 根據名稱模糊查詢用戶
    public List<UserBean> findUsersByName(String name) {
        return userRepo.findByUsernameContaining(name);
    }

    // 更新使用者資料
    public UserBean updateUser(UserBean user) {
        Optional<UserBean> optionalUser = userRepo.findById(user.getId());
        if (optionalUser.isPresent()) {
            UserBean existingUser = optionalUser.get();
            // 更新所有欄位
            existingUser.setUsername(user.getUsername());
            existingUser.setPassword(user.getPassword());
            existingUser.setEmail(user.getEmail());
            existingUser.setAvatar(user.getAvatar()); // 設置 avatar
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            existingUser.setGender(user.getGender());
            existingUser.setBirthday(user.getBirthday());
            existingUser.setWeight(user.getWeight());
            existingUser.setHeight(user.getHeight());

            // 保存更新後的使用者資料
            return userRepo.save(existingUser);
        }
        return null;
    }
    
 
    
  
    public UserBean updateUserWithoutPassword(UserBean user) {
        Optional<UserBean> optionalUser = userRepo.findById(user.getId());
        if (optionalUser.isPresent()) {
            UserBean existingUser = optionalUser.get();
            // 更新所有欄位
            existingUser.setUsername(user.getUsername());
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setAvatar(user.getAvatar()); // 設置 avatar
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            existingUser.setGender(user.getGender());
            existingUser.setBirthday(user.getBirthday());
            existingUser.setWeight(user.getWeight());
            existingUser.setHeight(user.getHeight());

            // 保存更新後的使用者資料
            return userRepo.save(existingUser);
        }
        return null;
    }
    
    public Optional<UserBean> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
    
    public Optional<UserBean> findByEmail(String email) {
    	   return userRepo.findByEmail(email);
    	}
    
    // 產生 Token 並寄送 Email
//    public boolean generateResetToken(String email) {
//        Optional<UserBean> optionalUser = userRepo.findByEmail(email);
//        if (optionalUser.isPresent()) {
//            UserBean user = optionalUser.get();
//            String token = UUID.randomUUID().toString(); // 產生隨機 Token
//            user.setResetToken(token);
//            userRepo.save(user);
//
//            // 發送 Email
//            sendResetEmail(user.getEmail(), token);
//            return true;
//        }
//        return false;
//    }
    
    public boolean generateResetToken(String email) {
        System.out.println("🔹 [generateResetToken] 嘗試為此 Email 產生 Token: " + email);

        Optional<UserBean> optionalUser = userRepo.findByEmail(email);
        
        if (optionalUser.isPresent()) {
            UserBean user = optionalUser.get();
            
            String token = UUID.randomUUID().toString(); // 產生隨機 Token
            System.out.println("✅ [generateResetToken] 產生的 Token: " + token);

            user.setResetToken(token);
            userRepo.save(user);

            // ✅ 檢查用戶是否成功存入 Token
            Optional<UserBean> updatedUser = userRepo.findByEmail(email);
            if (updatedUser.isPresent()) {
                System.out.println("🔹 [generateResetToken] 資料庫中 Token 確認儲存成功：" + updatedUser.get().getResetToken());
             // 發送 Email
                boolean sendResetEmailResult = sendResetEmail(user.getEmail(), token);                
                return sendResetEmailResult;
            } else {
                System.out.println("❌ [generateResetToken] Token 存入失敗！");
                return false;
            }
            
        } else {
            System.out.println("❌ [generateResetToken] 查無此 Email: " + email);
            return false;
        }
    }


    // 驗證 Token 並更新密碼
    public boolean resetPassword(String token, String newPassword) {
        Optional<UserBean> optionalUser = userRepo.findByResetToken(token);
        if (optionalUser.isPresent()) {
            UserBean user = optionalUser.get();
            user.setPassword(newPassword); // 密碼應該加密
            user.setResetToken(null); // 清除 Token，避免重複使用
            userRepo.save(user);
            return true;
        }
        return false;
    }

    // 發送密碼重設信
    
    


    
    public boolean sendResetEmail(String email, String token) {
        // 載入 .env 配置
        Dotenv dotenv = Dotenv.load();
        String sendGridApiKey = dotenv.get("SENDGRID_API_KEY");
        String senderEmail = dotenv.get("SENDER_EMAIL");

        if (sendGridApiKey == null || sendGridApiKey.isEmpty()) {
            System.out.println("❌ SendGrid API Key 未設置，請檢查 .env 檔案！");
            return false;
        }

        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        String subject = "重設密碼";
        String content = "請點擊以下連結來重設密碼: " + resetLink;

        Email from = new Email(senderEmail);
        Email toEmail = new Email(email);
        Content emailContent = new Content("text/plain", content);
        Mail mail = new Mail(from, subject, toEmail, emailContent);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("✅ Email 發送成功！狀態碼：" + response.getStatusCode());
            return true;

        } catch (Exception e) {
            System.out.println("❌ Email 發送失敗：" + e.getMessage());
            return false;
        }
    }
    
 // 獲取用戶的關注者數量
    public long getFollowersCount(Integer userId) {
        return userFollowRepository.countByFollowingId(userId);
    }

    // 獲取用戶正在關注的數量
    public long getFollowingCount(Integer userId) {
        return userFollowRepository.countByFollowerId(userId);
    }

    // 檢查是否已關注
    public boolean isFollowing(Integer followerId, Integer followingId) {
        return userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    // 處理關注/取消關注
    @Transactional
    public Map<String, Object> toggleFollow(Integer followerId, Integer followingId) {
        Map<String, Object> result = new HashMap<>();
        
        boolean isFollowing = isFollowing(followerId, followingId);
        if (isFollowing) {
            // 取消關注
            userFollowRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
            result.put("isFollowing", false);
        } else {
            // 新增關注
            UserFollow follow = new UserFollow();
            follow.setFollowerId(followerId);
            follow.setFollowingId(followingId);
            follow.setCreatedAt(new Date(followingId));
            userFollowRepository.save(follow);
            result.put("isFollowing", true);
        }
        
        // 更新關注者數量
        result.put("followersCount", getFollowersCount(followingId));
        
        return result;
    }

//    // 保存用戶頭像
//    public String saveAvatar(MultipartFile avatar) throws IOException {
//        String fileName = UUID.randomUUID().toString() + getFileExtension(avatar.getOriginalFilename());
//        Path uploadPath = Path.of(avatarUploadDir);
//        
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//        
//        Path filePath = uploadPath.resolve(fileName);
//        Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//        
//        return "/uploads/avatars/" + fileName;
//    }

    // 獲取文件副檔名
    private String getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".")))
                .orElse("");
    }

//    // 轉換為 DTO
//    public UserDTO convertToDTO(UserBean user) {
//        UserDTO dto = new UserDTO();
//        dto.setId(user.getId());
//        dto.setUsername(user.getUsername());
//        dto.setEmail(user.getEmail());
//        dto.setBio(user.getBio());
//        dto.setAvatar(user.getAvatar());
//        dto.setCreatedAt(user.getCreatedAt());
//        return dto;
//    }
    
}