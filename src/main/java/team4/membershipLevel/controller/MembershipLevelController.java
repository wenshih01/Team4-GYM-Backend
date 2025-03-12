package team4.membershipLevel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import team4.membershipLevel.model.MembershipLevel;
import team4.membershipLevel.model.MembershipLevelService;
import team4.userLevel.model.UserLevel;

@RestController
@RequestMapping("/api/membership-levels")

public class MembershipLevelController {

    @Autowired
    private MembershipLevelService membershipLevelService;

    // 取得所有會員等級
    @GetMapping
    public ResponseEntity<List<MembershipLevel>> getAllLevels() {
        List<MembershipLevel> levels = membershipLevelService.getAllLevels();
        return ResponseEntity.ok(levels);
    }

    // 依 ID 取得單筆會員等級
    @GetMapping("/{id}")
    public ResponseEntity<MembershipLevel> getLevelById(@PathVariable int id) {
        MembershipLevel level = membershipLevelService.getLevelById(id);
        return ResponseEntity.ok(level);
    }

    // 新增會員等級
    @PostMapping
    public ResponseEntity<MembershipLevel> createLevel(@RequestBody MembershipLevel level) {
        MembershipLevel newLevel = membershipLevelService.createLevel(level);
        return ResponseEntity.ok(newLevel);
    }

    // 更新會員等級
    @PutMapping("/{id}")
    public ResponseEntity<MembershipLevel> updateLevel(@PathVariable int id, @RequestBody MembershipLevel level) {
        MembershipLevel updatedLevel = membershipLevelService.updateLevel(id, level);
        return ResponseEntity.ok(updatedLevel);
    }

    // 刪除會員等級
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLevel(@PathVariable int id) {
        membershipLevelService.deleteLevel(id);
        return ResponseEntity.ok("會員等級 (ID: " + id + ") 已刪除");
    }
}