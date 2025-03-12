package team4.chatbot.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface ChatbotResponseRepository extends JpaRepository<ChatbotResponse, Integer> {
    // 使用 SQL LIKE 進行模糊搜尋，按關鍵字長度排序
    @Query("SELECT c FROM ChatbotResponse c WHERE LOWER(c.keyword) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY LENGTH(c.keyword) ASC")
    List<ChatbotResponse> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT c FROM ChatbotResponse c WHERE SOUNDEX(c.keyword) = SOUNDEX(:keyword)")
    List<ChatbotResponse> searchBySimilarKeyword(@Param("keyword") String keyword);
}