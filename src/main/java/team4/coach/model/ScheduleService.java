
package team4.coach.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team4.booking.model.Booking;
import team4.booking.model.BookingRepository;
import team4.howard.member.model.UserBean;
import team4.howard.member.model.UserRepository;
import team4.userLevel.model.UserLevel;
import team4.userLevel.model.UserLevelRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional

public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private CoachRepository coachRepository; // 引入 CoachRepository 用於查詢 Coach

    @Autowired
    private CourseRepository courseRepository; // 引入 CourseRepository 用於查詢 Course

    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserLevelRepository userLevelRepository;
    /**
     * 取得所有排程
     */
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    /**
     * 依據 ID 查詢單筆排程
     */
    public Schedule findById(Integer scheduleId) {
        return scheduleRepository.findById(scheduleId).orElse(null);
    }

    /**
     * 建立新排程 - 檢查「同課程」及「同教練」時間不可重疊
     */
    public Schedule createSchedule(ScheduleRequest req) {
        String empNo = req.getEmpno();
        Integer courseId = req.getCourseId();
        LocalDateTime start = req.getStartTime();
        LocalDateTime end = req.getEndTime();

        checkOverlapping(empNo, courseId, start, end);

        Coach coach = coachRepository.findById(empNo)
            .orElseThrow(() -> new RuntimeException("Coach not found: " + empNo));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));

        String status = calculateCourseStatus(start, end);

        Schedule schedule = new Schedule();
        schedule.setCoach(coach);
        schedule.setCourse(course);
        schedule.setStartTime(start);
        schedule.setEndTime(end);
        schedule.setStatus(status);
        schedule.setMaxParticipants(req.getMaxParticipants()); // 設定最大報名人數
        

        return scheduleRepository.save(schedule);
    }


    /**
     * 抽取出來的私有方法，檢查同一位教練或同一課程在指定時段是否已有排程
     */
    private void checkOverlapping(String empNo, Integer courseId, LocalDateTime start, LocalDateTime end) {
        // 查詢同教練是否在此區間有重疊
        var overlappingCoach = scheduleRepository.findOverlappingByCoach(empNo, start, end);
        if (!overlappingCoach.isEmpty()) {
            throw new IllegalArgumentException("同一位教練在此時段已有排程，不可重複安排！");
        }

        // 查詢同課程是否在此區間有重疊
        var overlappingCourse = scheduleRepository.findOverlappingByCourse(courseId, start, end);
        if (!overlappingCourse.isEmpty()) {
            throw new IllegalArgumentException("同一課程在此時段已經有排程，不可重複安排！");
        }
    }



    /**
     * 更新排程 (若業務邏輯相同，也可在這裡加上「衝突檢查」)
     */
    public Schedule updateSchedule(Integer scheduleId, ScheduleRequest req) {
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("找不到指定的排程 (scheduleId = " + scheduleId + ")"));

        LocalDateTime start = req.getStartTime();
        LocalDateTime end = req.getEndTime();

        checkOverlappingWhenUpdate(scheduleId, req.getEmpno(), req.getCourseId(), start, end);

        Coach coach = coachRepository.findById(req.getEmpno())
            .orElseThrow(() -> new RuntimeException("Coach not found: " + req.getEmpno()));
        Course course = courseRepository.findById(req.getCourseId())
            .orElseThrow(() -> new RuntimeException("Course not found: " + req.getCourseId()));

        String status = calculateCourseStatus(start, end);

        existingSchedule.setCoach(coach);
        existingSchedule.setCourse(course);
        existingSchedule.setStartTime(start);
        existingSchedule.setEndTime(end);
        existingSchedule.setStatus(status);
        existingSchedule.setMaxParticipants(req.getMaxParticipants());

        return scheduleRepository.save(existingSchedule);
    }
    
    public void updateAllCourseStatuses() {
        List<Schedule> schedules = scheduleRepository.findAll();
        for (Schedule schedule : schedules) {
            String updatedStatus = calculateCourseStatus(schedule.getStartTime(), schedule.getEndTime());
            schedule.setStatus(updatedStatus);
        }
        scheduleRepository.saveAll(schedules);
    }


    /**
     * 檢查更新時是否跟其他排程重疊 (如果需要)
     */
    private void checkOverlappingWhenUpdate(
            Integer selfScheduleId,
            String empNo, 
            Integer courseId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        // 查詢同教練在此區間的排程，但排除自己這筆 schedule
        var overlappingCoach = scheduleRepository.findOverlappingByCoachExcludeSelf(empNo, start, end, selfScheduleId);
        if (!overlappingCoach.isEmpty()) {
            throw new IllegalArgumentException("同一位教練在此時段已有排程，無法更新！");
        }

        // 查詢同課程在此區間的排程，但排除自己
        var overlappingCourse = scheduleRepository.findOverlappingByCourseExcludeSelf(courseId, start, end, selfScheduleId);
        if (!overlappingCourse.isEmpty()) {
            throw new IllegalArgumentException("同一課程在此時段已有排程，無法更新！");
        }
    }

    /**
     * 刪除
     */
    public void deleteSchedule(Integer scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
    
    //模糊查詢
    public List<Schedule> findByCourseName(String courseName) {
        return scheduleRepository.findByCourseNameContaining(courseName);
    }
    
    public List<Schedule> createRecurringSchedules(ScheduleRequest req, LocalDate startDate, LocalDate endDate) {
        List<Schedule> schedules = new ArrayList<>();

        // 計算從 startDate 到 endDate 的所有週期
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            LocalDateTime startTime = req.getStartTime().with(currentDate);
            LocalDateTime endTime = req.getEndTime().with(currentDate);

            // 檢查重疊
            checkOverlapping(req.getEmpno(), req.getCourseId(), startTime, endTime);

            // 創建新排程
            Coach coach = coachRepository.findById(req.getEmpno())
                .orElseThrow(() -> new RuntimeException("Coach not found: " + req.getEmpno()));
            Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found: " + req.getCourseId()));

            Schedule schedule = new Schedule();
            schedule.setCoach(coach);
            schedule.setCourse(course);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setStatus(req.getStatus());
            schedules.add(schedule);

            // 遞增到下一週
            currentDate = currentDate.plusWeeks(1);
        }

        // 批量保存到資料庫
        return scheduleRepository.saveAll(schedules);
    }
    
    public List<Schedule> createOrUpdateRecurringSchedules(ScheduleRequest req, LocalDate startDate, LocalDate endDate, Integer existingScheduleId) {
        List<Schedule> schedules = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            LocalDateTime startTime = req.getStartTime().with(currentDate);
            LocalDateTime endTime = req.getEndTime().with(currentDate);

            // 檢查是否衝突，對於更新操作需排除現有排程的 ID
            if (existingScheduleId != null) {
                checkOverlappingWhenUpdate(existingScheduleId, req.getEmpno(), req.getCourseId(), startTime, endTime);
            } else {
                checkOverlapping(req.getEmpno(), req.getCourseId(), startTime, endTime);
            }

            Coach coach = coachRepository.findById(req.getEmpno())
                .orElseThrow(() -> new RuntimeException("Coach not found: " + req.getEmpno()));
            Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found: " + req.getCourseId()));

            Schedule schedule = new Schedule();
            schedule.setCoach(coach);
            schedule.setCourse(course);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setStatus(req.getStatus());
            schedules.add(schedule);

            currentDate = currentDate.plusWeeks(1);
        }

        return scheduleRepository.saveAll(schedules);
    }
    
 // 每分鐘執行一次
    @Scheduled(cron = "0 * * * * ?")
    public void updateStatusesEveryMinute() {
        List<Schedule> schedules = scheduleRepository.findAll();
        for (Schedule schedule : schedules) {
            // 如果有手動狀態，跳過更新
            if (schedule.getManualStatus() != null) {
                continue;
            }

            // 計算狀態
            String updatedStatus = calculateCourseStatus(schedule.getStartTime(), schedule.getEndTime());
            // 如果狀態有變化，才進行更新
            if (!updatedStatus.equals(schedule.getStatus())) {
                schedule.setStatus(updatedStatus);
            }
        }
        scheduleRepository.saveAll(schedules);
        System.out.println("課程狀態已更新：" + LocalDateTime.now());
    }
    
    // 狀態計算方法
    private String calculateCourseStatus(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            return "未開始";
        } else if (now.isAfter(endTime)) {
            return "已結束";
        } else {
            return "進行中";
        }
    }
    
    /**
     * 取消課程
     * @param scheduleId 排程 ID
     */
    public void cancelSchedule(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("找不到指定的排程 (scheduleId = " + scheduleId + ")"));

        if ("已結束".equals(schedule.getStatus())) {
            throw new RuntimeException("此課程已結束，無法更改狀態！");
        }

        schedule.setManualStatus("停課");
        schedule.setStatus("停課");
        scheduleRepository.save(schedule);
        System.out.println("✅ 課程 " + scheduleId + " 已停課");

        List<Booking> bookings = bookingRepository.findBookingsByScheduleId(scheduleId);
        System.out.println("🔍 找到 " + bookings.size() + " 筆預約");

        for (Booking booking : bookings) {
            if ("已預約".equals(booking.getStatus())) { 
                booking.setStatus("停課");

                // **退還點數到 user_level**
                UserLevel userLevel = userLevelRepository.findByUserId(booking.getUser().getId())
                        .orElseThrow(() -> new RuntimeException("找不到該會員的等級資訊"));

                userLevel.setPoints(userLevel.getPoints() + 1); // 加回 1 點
                userLevelRepository.save(userLevel);

                System.out.println("🎯 會員 " + userLevel.getUser().getId() + " 點數退還 1 點，目前點數：" + userLevel.getPoints());

                bookingRepository.save(booking);
            }
        }
    }
    




    public void restoreSchedule(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("找不到指定的排程 (scheduleId = " + scheduleId + ")"));

        schedule.setManualStatus(null);

        // **根據當前時間計算狀態**
        String updatedStatus = calculateCourseStatus(schedule.getStartTime(), schedule.getEndTime());
        schedule.setStatus(updatedStatus);
        scheduleRepository.save(schedule);

        List<Booking> bookings = bookingRepository.findBookingsByScheduleId(scheduleId);
        for (Booking booking : bookings) {
            if ("停課".equals(booking.getStatus())) {
                booking.setStatus("已預約");

                // **扣除點數**
                UserLevel userLevel = userLevelRepository.findByUserId(booking.getUser().getId())
                        .orElseThrow(() -> new RuntimeException("找不到該會員的等級資訊"));

                if (userLevel.getPoints() > 0) { // 防止負數
                    userLevel.setPoints(userLevel.getPoints() - 1);
                    userLevelRepository.save(userLevel);
                    System.out.println("📌 會員 " + userLevel.getUser().getId() + " 恢復課程，扣除 1 點，目前點數：" + userLevel.getPoints());
                } else {
                    System.out.println("⚠️ 會員 " + userLevel.getUser().getId() + " 點數不足，無法扣除！");
                }

                bookingRepository.save(booking);
            }
        }
    }



}

