package team4.chatbot.model;

import jakarta.persistence.*; // ✅ 正確的 JPA 匯入
import lombok.Data;

@Entity
@Table(name = "chatbot_responses")
@Data
public class ChatbotResponse {

    @Id  // ✅ 確保這是 JPA 的 Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    private String response;

}
