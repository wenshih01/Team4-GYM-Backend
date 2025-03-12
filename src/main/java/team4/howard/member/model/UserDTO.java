package team4.howard.member.model;

import java.util.Date;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String email;
    private String bio;
    private String avatar;
    private Date createdAt;

    // Getters and Setters
}
