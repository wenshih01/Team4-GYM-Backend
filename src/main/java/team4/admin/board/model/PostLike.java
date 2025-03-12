package team4.admin.board.model;

import jakarta.persistence.*;
import lombok.Data;
import team4.howard.member.model.UserBean;
import java.util.Date;

@Entity
@Table(name = "PostLikes")
@Data
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserBean user;

    @Column(name = "created_at")
    private Date createdAt = new Date();
}