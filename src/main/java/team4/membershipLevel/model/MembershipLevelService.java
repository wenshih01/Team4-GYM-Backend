package team4.membershipLevel.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import team4.howard.member.model.UserRepository;
import team4.userLevel.model.UserLevelRepository;



@Service
@Transactional
public class MembershipLevelService {

    @Autowired
    private MembershipLevelRepository membershipLevelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLevelRepository userLevelRepository;

    public List<MembershipLevel> getAllLevels() {
        return membershipLevelRepository.findAll();
    }

    // 依 ID 取得單筆會員等級
    public MembershipLevel getLevelById(int levelId) {
        return membershipLevelRepository.findById(levelId)
                .orElseThrow(() -> new RuntimeException("找不到會員等級 (levelId = " + levelId + ")"));
    }

    // 新增會員等級
    public MembershipLevel createLevel(MembershipLevel level) {
        return membershipLevelRepository.save(level);
    }

    // 更新會員等級
    public MembershipLevel updateLevel(int levelId, MembershipLevel levelDetails) {
        MembershipLevel existingLevel = getLevelById(levelId);
        existingLevel.setLevelName(levelDetails.getLevelName());
        existingLevel.setMaxPoints(levelDetails.getMaxPoints());
        existingLevel.setPrice(levelDetails.getPrice());
        return membershipLevelRepository.save(existingLevel);
    }

    // 刪除會員等級
    public void deleteLevel(int levelId) {
        MembershipLevel level = getLevelById(levelId);
        membershipLevelRepository.delete(level);
    }
}

