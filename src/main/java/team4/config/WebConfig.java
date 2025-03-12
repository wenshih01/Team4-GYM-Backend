package team4.config;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // 前端的地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
        
        // ✅ 針對 ECPay API 增加 CORS 設定
        registry.addMapping("/api/ecpay/**")
                .allowedOrigins("http://localhost:5173") // 設定前端來源
                .allowedMethods("POST") // ECPay 主要用 POST
                .allowedHeaders("*")
                .allowCredentials(true);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 允許 /uploads/ 開頭的 URL 指向 uploads 資料夾
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        // 允許 /shop/uploads/ 開頭的 URL 指向 C:/temp/upload/
        registry.addResourceHandler("/shop/uploads/**")
                .addResourceLocations("file:C:/temp/upload/");
    }
    
    
    
    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.sendgrid.net");
        mailSender.setPort(587);
        mailSender.setUsername("apikey");  // SendGrid 這裡固定填入 apikey
        mailSender.setPassword("AQQ1M4SZA2YEBXUQQ64RJFEA");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        return mailSender;
    }
}