package team4.chatbot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import team4.chatbot.model.ChatbotResponse;
import team4.chatbot.model.ChatbotResponseRepository;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team4.chatbot.model.ChatbotResponse;
import team4.chatbot.model.ChatbotResponseRepository;
import java.util.List;

@Service
public class ChatbotService {
    @Autowired
    private ChatbotResponseRepository chatbotResponseRepository;

    public String getResponse(String userMessage) {
        // 1. 使用 LIKE 查詢可能匹配的關鍵字
        List<ChatbotResponse> matchedResponses = chatbotResponseRepository.searchByKeyword(userMessage);

        if (matchedResponses.isEmpty()) {
            return "抱歉，我不太明白您的問題，請換個方式問問看！";
        }

        // 2. 使用 Levenshtein Distance 找最相似的詞
        LevenshteinDistance levenshtein = new LevenshteinDistance();
        ChatbotResponse bestMatch = null;
        int bestScore = Integer.MAX_VALUE;

        for (ChatbotResponse response : matchedResponses) {
            int distance = levenshtein.apply(userMessage, response.getKeyword());
            if (distance < bestScore) {
                bestScore = distance;
                bestMatch = response;
            }
        }

        // 3. 如果相似度夠高（小於 503），回傳最佳結果
        if (bestMatch != null && bestScore < 50) {
            return bestMatch.getResponse();
        }

        // 4. 如果沒有找到合適的匹配，回傳預設回應
        return "抱歉，我找不到相關資訊。";
    }
}
