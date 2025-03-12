package team4.userLevel.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team4.howard.member.model.UserBean;
import team4.howard.member.model.UserRepository;
import team4.membershipLevel.model.MembershipLevel;
import team4.membershipLevel.model.MembershipLevelRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserLevelService {

    @Autowired
    private UserLevelRepository userLevelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MembershipLevelRepository membershipRepository;

    /**
     * 🔍 取得所有會員等級資訊
     */
    public List<UserLevel> getAllUserLevels() {
        return userLevelRepository.findAll();
    }

    /**
     * 🔍 取得會員的 UserLevel
     */
    public UserLevel getUserLevel(Integer userId) {
        return userLevelRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("找不到該會員的等級資訊"));
    }

    /**
     * 🛒 會員購買或更換會員等級 (點數隨等級變動)
     * - 設定 `startDate` 為當天
     * - 設定 `expiryDate` 為 `startDate` 加上等級內設定的月份
     */
    public UserLevel purchaseMembership(Integer userId, Integer levelId) {
        UserBean user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("會員未找到"));

        MembershipLevel membershipLevel = membershipRepository.findById(levelId)
            .orElseThrow(() -> new RuntimeException("會員等級未找到"));

        Optional<UserLevel> existingUserLevel = userLevelRepository.findByUserId(userId);

        LocalDate startDate = LocalDate.now();
        LocalDate expiryDate = startDate.plusMonths(membershipLevel.getDurationMonths()); // 依據方案時長計算

        if (existingUserLevel.isPresent()) {
            // **如果已經有會員等級，則更新**
            UserLevel userLevel = existingUserLevel.get();
            userLevel.setMembershipLevel(membershipLevel);
            userLevel.setPoints(membershipLevel.getMaxPoints());  // 🔄 點數隨等級變動
            userLevel.setStartDate(startDate);
            userLevel.setExpiryDate(expiryDate);
            return userLevelRepository.save(userLevel);
        } else {
            // **如果沒有會員等級，則新建**
            UserLevel newUserLevel = new UserLevel();
            newUserLevel.setUser(user);
            newUserLevel.setMembershipLevel(membershipLevel);
            newUserLevel.setPoints(membershipLevel.getMaxPoints()); // ✅ 點數 = maxPoints
            newUserLevel.setStartDate(startDate);
            newUserLevel.setExpiryDate(expiryDate);
            return userLevelRepository.save(newUserLevel);
        }
    }

    /**
     * ✏️ 修改會員等級 (點數同步變動)
     */
    public UserLevel updateMembership(Integer userId, Integer levelId) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("找不到該會員的等級資訊"));

        MembershipLevel membershipLevel = membershipRepository.findById(levelId)
            .orElseThrow(() -> new RuntimeException("會員等級未找到"));

        LocalDate startDate = LocalDate.now();
        LocalDate expiryDate = startDate.plusMonths(membershipLevel.getDurationMonths());

        userLevel.setMembershipLevel(membershipLevel);
        userLevel.setPoints(membershipLevel.getMaxPoints()); // 🔄 點數同步更新
        userLevel.setStartDate(startDate);
        userLevel.setExpiryDate(expiryDate);
        return userLevelRepository.save(userLevel);
    }

    /**
     * 🔄 每天執行：檢查過期會員並降級為 Basic
     * - 每天凌晨 00:00:00 檢查
     */
    @Scheduled(cron = "0 0 0 * * ?") // 每天執行一次
    public void downgradeExpiredMemberships() {
        List<UserLevel> userLevels = userLevelRepository.findAll();
        MembershipLevel basicLevel = membershipRepository.findByLevelName("Basic")
            .orElseThrow(() -> new RuntimeException("找不到 Basic 會員等級"));

        for (UserLevel userLevel : userLevels) {
            if (userLevel.getExpiryDate() != null && userLevel.getExpiryDate().isBefore(LocalDate.now())) {
                System.out.println("⏳ 會員 " + userLevel.getUser().getName() + " 會員等級過期，降級至 Basic");
                userLevel.setMembershipLevel(basicLevel);
                userLevel.setPoints(basicLevel.getMaxPoints());
                userLevel.setStartDate(null);
                userLevel.setExpiryDate(null);
                userLevelRepository.save(userLevel);
            }
        }
    }

    /**
     * ❌ 刪除會員等級
     */
    public void deleteUserLevel(Integer userId) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("找不到該會員的等級資訊"));

        userLevelRepository.delete(userLevel);
    }
}
