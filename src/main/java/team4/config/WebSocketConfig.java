package team4.config;

import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker  // 啟用 WebSocket 訊息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 啟用簡單的訊息代理
        config.enableSimpleBroker("/queue", "/topic");
        
        // 設置應用程式的目標前綴
        config.setApplicationDestinationPrefixes("/app");
        
        // 設置用戶目標的前綴
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 註冊 STOMP 端點
        registry.addEndpoint("/ws")
               .setAllowedOrigins("http://localhost:5173")  // 允許的前端來源
               .withSockJS() 
               .setInterceptors(new HandshakeInterceptor() {
                   @Override
                   public boolean beforeHandshake(ServerHttpRequest request, 
                       ServerHttpResponse response, WebSocketHandler wsHandler, 
                       Map<String, Object> attributes) throws Exception {
                       // 可以在這裡添加驗證邏輯
                       return true;
                   }

                   @Override
                   public void afterHandshake(ServerHttpRequest request, 
                       ServerHttpResponse response, WebSocketHandler wsHandler, 
                       Exception exception) {
                   }
              });// 啟用 SockJS
    }
}