package team4.admin.board.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import team4.community.dto.LoginRequest;
import team4.howard.member.model.*;
import team4.train.util.JwtUtil;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session, HttpServletResponse response) {
        return userRepository.findByUsername(request.getUsername())
            .map(user -> {
                if (user.getPassword().equals(request.getPassword())) {
                    String token = JwtUtil.generateToken(user.getUsername());

                    // 設置 Session
                    session.setAttribute("username", user.getUsername());
                    session.setAttribute("userId", user.getId());
                    
                    // 設置 Cookie (Session ID)
                    Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
                    sessionCookie.setHttpOnly(true); // 防止 JavaScript 讀取
                    sessionCookie.setPath("/"); // 讓所有 API 都能使用
                    sessionCookie.setMaxAge(60 * 60); // 1 小時
                    response.addCookie(sessionCookie);

                    System.out.println("Login API: Session ID: " + session.getId());
                    System.out.println("Login API: Session Username (after set): " + session.getAttribute("username"));

                    return ResponseEntity.ok(Map.of(
                        "token", token,
                        "user", Map.of("id", user.getId(), "username", user.getUsername())
                        
                    ));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid username or password"));
                }
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid username or password")));
    }

    
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request, HttpSession session) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println("Received Cookie: " + cookie.getName() + " = " + cookie.getValue());
            }
        }

        System.out.println("Profile API: Session ID: " + session.getId());
        System.out.println("Profile API: Session Username: " + session.getAttribute("username"));

        Object username = session.getAttribute("username");

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No active session"));
        }

        Optional<UserBean> user = userService.findByUsername(username.toString());

        if (user.isPresent()) {
            return ResponseEntity.ok(Map.of(
                "id", user.get().getId(),
                "username", user.get().getUsername(),
                "email", user.get().getEmail(),
                "avatar", user.get().getAvatar(),
                "name",user.get().getName()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, 
            SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok().body(Map.of("message", "登出成功"));
    }
    
    
    @GetMapping("/check")
    public ResponseEntity<?> checkLoginStatus(HttpSession session) {
       String username = (String) session.getAttribute("username");
       Integer userId = (Integer) session.getAttribute("userId");

       System.out.println("檢查登入狀態 - 使用者名稱: " + username);
       System.out.println("檢查登入狀態 - 使用者ID: " + userId);

       if (username != null && userId != null) {
           Optional<UserBean> userOpt = userService.findByUsername(username);
           if (userOpt.isPresent()) {
               UserBean user = userOpt.get();
               
               System.out.println("使用者角色: " + user.getRole());
               boolean isAdmin = user.getRole().equals("ADMIN");
               System.out.println("是否為管理員: " + isAdmin);

               return ResponseEntity.ok(Map.of(
                   "isLoggedIn", true,
                   "user", Map.of(
                       "id", user.getId(),
                       "username", user.getUsername(),
                       "avatar", user.getAvatar(),
                       "isAdmin", isAdmin
                   )
               ));
           }
       }
       return ResponseEntity.ok(Map.of("isLoggedIn", false));
    }
    
    @PostMapping("/check-duplicate")
    public ResponseEntity<?> checkDuplicate(@RequestParam String field, @RequestParam String value) {
        if("username".equals(field)) {
            return ResponseEntity.ok(userService.findByUsername(value).isPresent());
        } else if("email".equals(field)) {
            return ResponseEntity.ok(userService.findByEmail(value).isPresent());
        }
        return ResponseEntity.badRequest().build();
    }

}