package team4.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import team4.chatbot.service.ChatbotService;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    /**
     * GET /chatbot/reply?message=使用者訊息
     * 呼叫後台服務取得回覆
     *
     * @param message 使用者傳入訊息
     * @return 回覆內容
     */
    @GetMapping("/reply")
    public ResponseEntity<String> getReply(@RequestParam String message) {
        try {
            String response = chatbotService.getResponse(message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("伺服器錯誤：" + e.getMessage());
        }
    }
}
