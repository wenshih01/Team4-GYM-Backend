package team4.howard.member.payment.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody Map<String, String> request) {
        try {
            String priceId = request.get("priceId");

            System.out.println("🔍 接收到的 priceId: " + priceId);

            if (priceId == null || priceId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ priceId 不能為空！");
            }

            // Stripe 訂閱模式
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION) // ✅ 確保模式是 "SUBSCRIPTION"
                    .setSuccessUrl("http://localhost:5173/success?session_id={CHECKOUT_SESSION_ID}") // ✅ 確保 success URL 有 session_id
                    .setCancelUrl("http://localhost:5173/cancel")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(priceId)
                                    .setQuantity(1L)
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);

            System.out.println("✅ 成功建立 Session，ID: " + session.getId());

            Map<String, String> response = new HashMap<>();
            response.put("sessionId", session.getId());

            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            System.err.println("❌ Stripe API 錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Stripe API 錯誤: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ 伺服器錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ 伺服器錯誤: " + e.getMessage());
        }
    }
}
