package team4.shopping.utils;

import java.util.Random;

public class IdGenerator {

    private static final Random random = new Random();

    // 生成 10 位數字的隨機 ID
    public static Long generateUniqueId() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10)); // 隨機生成 0-9 的數字
        }
        return Long.parseLong(sb.toString());
    }
}
