package team4.train.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;



import io.jsonwebtoken.Claims;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;


public class JwtUtil {

    // 自動生成合適的密鑰（長度與算法匹配）
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token 有效期（1 天）
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    /**
     * 生成 JWT Token
     *
     * @param username 用戶名
     * @return JWT Token
     */
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // 設置主體（用戶名）
                .setIssuedAt(new Date()) // 簽發時間
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 過期時間
                .signWith(SECRET_KEY) // 使用密鑰簽名
                .compact();
    }

    /**
     * 驗證 Token 並提取數據
     *
     * @param token JWT Token
     * @return Claims（包含 token 中的數據）
     */
    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY) // 設置密鑰
                .build()
                .parseClaimsJws(token) // 解析 Token
                .getBody();
    }

    /**
     * 從 Token 中提取用戶名
     *
     * @param token JWT Token
     * @return 用戶名
     */
    public static String extractUsername(String token) {
        return validateToken(token).getSubject();
    }

    /**
     * 檢查 Token 是否已過期
     *
     * @param token JWT Token
     * @return 是否過期
     */
    public static boolean isTokenExpired(String token) {
        Date expiration = validateToken(token).getExpiration();
        return expiration.before(new Date());
    }
}