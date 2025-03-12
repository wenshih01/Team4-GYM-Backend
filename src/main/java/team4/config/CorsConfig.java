package team4.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 匹配所有路徑
                .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5173") // 允許的前端地址
                .allowedMethods("*") // 允許所有 HTTP 方法
                .allowedHeaders("*") // 允許所有請求頭
                .allowCredentials(true) // 允許攜帶 Cookie
                .maxAge(3600); // 預檢請求有效期
    }
}
