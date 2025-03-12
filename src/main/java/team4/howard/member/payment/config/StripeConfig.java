package team4.howard.member.payment.config;

import com.stripe.Stripe;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;


@Component
public class StripeConfig {
    private static final String STRIPE_SECRET_KEY = "sk_test_51QofzSQtZ3XT0Gdvm1K4LWHoi3TaYBzZj1qBdXnGqiApM3HtGo9G67o3xWAvIkGY9V5Kv8Xqq2ODS3QaJDhzLBKS00eGG02PpL"; // 你的 Secret Key

    @PostConstruct
    public void init() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
        System.out.println("✅ Stripe API 初始化完成");
    }
}
