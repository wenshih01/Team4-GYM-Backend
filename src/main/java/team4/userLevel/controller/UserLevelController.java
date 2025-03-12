package team4.userLevel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team4.howard.member.model.UserBean;
import team4.howard.member.model.UserRepository;
import team4.membershipLevel.model.MembershipLevel;
import team4.membershipLevel.model.MembershipLevelRepository;
import team4.userLevel.model.UserLevel;
import team4.userLevel.model.UserLevelService;

import java.util.List;

@RestController
@RequestMapping("/api/user-level")
@CrossOrigin
public class UserLevelController {

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MembershipLevelRepository membershipLevelRepository;

    /**
     * 🔍 取得所有會員等級 (UserLevel)
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserLevel>> getAllUserLevels() {
        return ResponseEntity.ok(userLevelService.getAllUserLevels());
    }

    /**
     * 🔍 取得所有會員 (用於下拉選單)
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserBean>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    /**
     * 🔍 取得所有會員等級 (用於下拉選單)
     */
    @GetMapping("/membership-levels")
    public ResponseEntity<List<MembershipLevel>> getAllMembershipLevels() {
        return ResponseEntity.ok(membershipLevelRepository.findAll());
    }

    /**
     * 🔍 取得會員的等級資訊
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserLevel> getUserLevel(@PathVariable Integer userId) {
        return ResponseEntity.ok(userLevelService.getUserLevel(userId));
    }

    /**
     * 🛒 會員購買或更換會員等級
     */
    @PostMapping("/purchase")
    public ResponseEntity<UserLevel> purchaseMembership(
        @RequestParam Integer userId,
        @RequestParam Integer levelId
    ) {
        return ResponseEntity.ok(userLevelService.purchaseMembership(userId, levelId));
    }

    /**
     * ✏️ 修改會員等級
     */
    @PutMapping("/update")
    public ResponseEntity<UserLevel> updateMembership(
        @RequestParam Integer userId,
        @RequestParam Integer levelId
    ) {
        return ResponseEntity.ok(userLevelService.updateMembership(userId, levelId));
    }

    /**
     * ❌ 刪除會員等級
     */
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUserLevel(@PathVariable Integer userId) {
        userLevelService.deleteUserLevel(userId);
        return ResponseEntity.ok("會員等級已刪除");
    }
}
