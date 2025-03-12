
package team4.coach.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team4.coach.model.Coach;
import team4.coach.model.CoachService;
import team4.coach.model.Schedule;
import team4.coach.model.ScheduleRepository;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/coaches")
public class CoachController {

    @Autowired
    private CoachService coachService;
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    

    @GetMapping("/findAll")
    public ResponseEntity<List<Coach>> getAllCoaches() {
        List<Coach> coaches = coachService.findAll();
        
        for (Coach coach : coaches) {
            if (coach.getPhoto() != null) {
                String photoUrl = "http://localhost:8082/uploads/" 
                                  + new File(coach.getPhoto()).getName();
                coach.setPhoto(photoUrl);
            }
        }
        return ResponseEntity.ok(coaches);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Coach> getCoachById(@PathVariable("id") String id) {
        Coach coach = coachService.findById(id);
        if (coach != null) {
            // 構造完整的照片 URL
            if (coach.getPhoto() != null) {
                String photoUrl = "http://localhost:8082/uploads/" 
                                  + new File(coach.getPhoto()).getName();
                coach.setPhoto(photoUrl);
            }
            return ResponseEntity.ok(coach);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertCoach(
            @RequestParam("empno") String empno,
            @RequestParam("ename") String ename,
            @RequestParam("nickname") String nickname,
            @RequestParam("salary") String salary,
            @RequestParam("hiredate") String hiredate,
            @RequestParam("title") String title,
            @RequestParam("skill") String skill,
            @RequestParam("experience") String experience,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {

        String photoPath = null;
        try {
            // 檔案儲存位置（絕對路徑）
            String uploadDir = System.getProperty("user.dir") + "/uploads"; // 以專案根目錄為基準
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdir(); // 如果目錄不存在，則建立
            }

            // 處理照片上傳
            if (photo != null && !photo.isEmpty()) {
                String fileName = photo.getOriginalFilename();
                photoPath = uploadDir + "/" + fileName;
                File dest = new File(photoPath);
                photo.transferTo(dest); // 將照片儲存到指定位置
            }

            // 建立 Coach 資料
            Coach coach = new Coach(empno, ename, nickname, salary, hiredate, title, skill, photoPath, experience);
            coachService.insert(coach);
            return ResponseEntity.status(HttpStatus.CREATED).body("教練資料插入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("插入失敗：" + e.getMessage());
        }
    }


    @PutMapping("/update/{empno}")
    public ResponseEntity<String> updateCoach(
            @PathVariable("empno") String empno,
            @RequestParam("ename") String ename,
            @RequestParam("nickname") String nickname,
            @RequestParam("salary") String salary,
            @RequestParam("hiredate") String hiredate,
            @RequestParam("title") String title,
            @RequestParam("skill") String skill,
            @RequestParam("experience") String experience,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {

        try {
            // 查找現有的教練資料
            Coach existingCoach = coachService.findById(empno);
            if (existingCoach == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("教練編號 " + empno + " 不存在");
            }

            String photoPath = existingCoach.getPhoto(); // 保留現有的照片路徑
            // 如果有新的照片上傳，則更新照片
            if (photo != null && !photo.isEmpty()) {
                String uploadDir = System.getProperty("user.dir") + "/uploads";
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdir(); // 如果目錄不存在，則建立
                }

                String fileName = photo.getOriginalFilename();
                photoPath = uploadDir + "/" + fileName;
                File dest = new File(photoPath);
                photo.transferTo(dest); // 將新照片儲存到指定位置
            }

            // 更新教練資料
            existingCoach.setEname(ename);
            existingCoach.setNickname(nickname);
            existingCoach.setSalary(salary);
            existingCoach.setHiredate(hiredate);
            existingCoach.setTitle(title);
            existingCoach.setSkill(skill);
            existingCoach.setExperience(experience);
            existingCoach.setPhoto(photoPath);

            // 保存更新後的教練資料
            coachService.update(existingCoach);
            return ResponseEntity.ok("教練資料更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失敗：" + e.getMessage());
        }
    }

    @DeleteMapping("/DeleteEmp")
    public String deleteCoach(@RequestParam("empno") String empno) {
        try {
            coachService.deleteById(empno);
            return "教練編號 " + empno + " 的資料已成功刪除。";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("刪除失敗：" + e.getMessage());
        }
    }
    
    @GetMapping("/searchByName")
    public ResponseEntity<List<Coach>> searchCoachesByName(@RequestParam("ename") String ename) {
        List<Coach> result = coachService.findByEnameContaining(ename);
        // 依照您現有的做法，如果有 photo，需要組合成完整 url
        for (Coach coach : result) {
            if (coach.getPhoto() != null) {
                String photoUrl = "http://localhost:8082/uploads/"
                                    + new File(coach.getPhoto()).getName();
                coach.setPhoto(photoUrl);
            }
        }
        return ResponseEntity.ok(result);
    }
    

    @GetMapping("/{empno}/schedules")
    public ResponseEntity<List<Schedule>> getAllSchedulesByCoach(@PathVariable("empno") String empno) {
        List<Schedule> schedules = scheduleRepository.findByCoachEmpno(empno);
        if (schedules.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(schedules);
    }

}