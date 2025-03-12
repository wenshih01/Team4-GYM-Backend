package team4.admin.board.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team4.howard.member.model.UserBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PostID")
    private int postId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userID", referencedColumnName = "id")
    private UserBean user;  // 改為 UserBean 類型

    @Column(name = "Content", columnDefinition = "NVARCHAR(MAX)")
    private String content; // Content 列可以為 NULL

    @Column(name = "LikesCount", nullable = false)
    private int likesCount = 0; // 預設值為 0

    @Column(name = "CommentsCount", nullable = false)
    private int commentsCount = 0; // 預設值為 0

    @Column(name = "Reported", nullable = false)
    private boolean reported = false; // 預設值為 false
    
    @Column(name = "ReportReason", columnDefinition = "NVARCHAR(500)")
    private String reportReason;

    @Column(name = "ReportedBy")
    private Integer reportedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private Date createdAt = new Date(); // 預設為當前時間，且不可更新

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UpdatedAt", nullable = false)
    private Date updatedAt = new Date(); // 預設為當前時間

    @Column(name = "ReviewStatus", length = 10)
    private String reviewStatus; // 可以為 NULL，長度為 10
    
    @Column(name = "is_visible", nullable = false)
    @Builder.Default
    private Boolean visible = true;

    // 一對多關係示例（假設有關聯圖片的表）
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "Post_Images",
        joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "image_url", length = 500)
    private List<String> imageUrls = new ArrayList<>();; // 假設有一張圖片 URL 的集合表
    

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();
}