package team4.howard.member.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Component
@Data  // 使用 Lombok
public class UserBean {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;
   
   @Column(nullable = false, unique = true)
   private String username;
   
   @Column(nullable = false)
   @JsonIgnore
   private String password;
   
   @Column(nullable = false)
   private String name;
   
   @Column(nullable = false, unique = true) 
   private String email;
   
   @Column(nullable = false)
   private LocalDate birthday;
   
   private String address;
   
   @Column(nullable = false)
   private String phone;
   
   @Column(nullable = false)
   private String gender;
   
   private Float weight;
   private Float height;
   
   @Column(name = "avatar")
   private String avatar;
   
   @Column(length = 10)
   private String role = "USER";  // 加入角色欄位

   @Column(name = "reset_token")
   private String resetToken;
   
   @Column(name = "membership_level", nullable = false)
   private String membershipLevel = "basic"; // 會員等級：basic, standard, premium
   
   
   @Override
   public String toString() {
       return "UserBean{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", name='" + name + '\'' +
               ", avatar='" + avatar + '\'' +
               // ... 其他欄位
               '}';
   }
   // 建構子保留不變
}