package team4.booking.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import team4.coach.model.Schedule;
import team4.coach.model.ScheduleRepository;
import team4.howard.member.model.UserBean;
import team4.howard.member.model.UserRepository;
import team4.userLevel.model.UserLevel;
import team4.userLevel.model.UserLevelRepository;

@Service
@Transactional
public class BookingService {

	 @Autowired
	    private BookingRepository bookingRepository;

	    @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private ScheduleRepository scheduleRepository;
	    
	    @Autowired
	    private UserLevelRepository userLevelRepository;

	    // 新增預約
	 
	    public Booking createBooking(Integer userId, Integer scheduleId) {
	        UserBean user = userRepository.findById(userId)
	                .orElseThrow(() -> new RuntimeException("會員不存在！"));

	        Schedule schedule = scheduleRepository.findById(scheduleId)
	                .orElseThrow(() -> new RuntimeException("課程排程不存在！"));
	        
	     // **🔴 檢查該會員是否已預約此課程**
	        boolean alreadyBooked = bookingRepository.existsByUser_IdAndSchedule_ScheduleId(userId, scheduleId);
	        if (alreadyBooked) {
	            throw new RuntimeException("您已預約此課程，請勿重複預約！");
	        }
	        

	        // **🔴 限制課程只能預約 "未開始" 狀態**
	        if (!"未開始".equals(schedule.getStatus())) {
	            throw new RuntimeException("該課程目前無法預約，狀態：" + schedule.getStatus());
	        }

	        // **檢查報名人數是否已滿**
	        if (schedule.getCurrentParticipants() >= schedule.getMaxParticipants()) {
	            throw new RuntimeException("報名失敗！該課程已達最大人數。");
	        }

	        // 🔹 取得會員的 UserLevel
	        UserLevel userLevel = userLevelRepository.findByUserId(userId)
	                .orElseThrow(() -> new RuntimeException("找不到該會員的等級資訊"));

	        // **檢查會員點數**
	        if (userLevel.getPoints() < 1) {
	            throw new RuntimeException("您以達到本月最大上課次數，無法完成預約！");
	        }

	        // ✅ **扣除點數**
	        userLevel.setPoints(userLevel.getPoints() - 1);
	        userLevelRepository.save(userLevel);

	        // ✅ **建立預約**
	        Booking booking = new Booking();
	        booking.setUser(user);
	        booking.setSchedule(schedule);
	        booking.setStatus("已預約");

	        // **增加報名人數**
	        schedule.setCurrentParticipants(schedule.getCurrentParticipants() + 1);
	        scheduleRepository.save(schedule);

	        return bookingRepository.save(booking);
	    }

	    // 🔹 取消預約 (退還點數)
	    public void cancelBooking(Integer bookingId) {
	        Booking booking = bookingRepository.findById(bookingId)
	                .orElseThrow(() -> new RuntimeException("預約記錄不存在！"));

	        // 確保只能取消 "已預約" 狀態的預約
	        if (!"已預約".equals(booking.getStatus())) {
	            throw new RuntimeException("此預約無法取消！");
	        }

	        // 更新狀態
	        booking.setStatus("已取消");

	        // 🔹 取得會員的 UserLevel
	        UserLevel userLevel = userLevelRepository.findByUserId(booking.getUser().getId())
	                .orElseThrow(() -> new RuntimeException("找不到該會員的等級資訊"));

	        // ✅ **退還點數**
	        userLevel.setPoints(userLevel.getPoints() + 1);
	        userLevelRepository.save(userLevel);

	        // 減少課程人數
	        Schedule schedule = booking.getSchedule();
	        schedule.setCurrentParticipants(schedule.getCurrentParticipants() - 1);
	        scheduleRepository.save(schedule);

	        bookingRepository.save(booking);
	    }

	    // 🔹 恢復預約 (扣除點數)
	    public void restoreBooking(Integer bookingId) {
	        Booking booking = bookingRepository.findById(bookingId)
	                .orElseThrow(() -> new RuntimeException("預約記錄不存在！"));

	        // 確保只能恢復 "已取消" 狀態的預約
	        if (!"已取消".equals(booking.getStatus())) {
	            throw new RuntimeException("此預約無法恢復！");
	        }

	        Schedule schedule = booking.getSchedule();

	        // **確保課程仍可預約**
	        if (!"未開始".equals(schedule.getStatus())) {
	            throw new RuntimeException("此課程目前無法恢復預約，狀態：" + schedule.getStatus());
	        }

	        // **確保課程還有名額**
	        if (schedule.getCurrentParticipants() >= schedule.getMaxParticipants()) {
	            throw new RuntimeException("此課程已滿員，無法恢復預約！");
	        }

	        // 🔹 取得會員的 UserLevel
	        UserLevel userLevel = userLevelRepository.findByUserId(booking.getUser().getId())
	                .orElseThrow(() -> new RuntimeException("找不到該會員的等級資訊"));

	        // **檢查會員點數**
	        if (userLevel.getPoints() < 1) {
	            throw new RuntimeException("點數不足，無法恢復預約！");
	        }

	        // ✅ **扣除點數**
	        userLevel.setPoints(userLevel.getPoints() - 1);
	        userLevelRepository.save(userLevel);

	        // 恢復預約狀態
	        booking.setStatus("已預約");

	        // 增加課程人數
	        schedule.setCurrentParticipants(schedule.getCurrentParticipants() + 1);
	        scheduleRepository.save(schedule);

	        bookingRepository.save(booking);
	    }
	


	    
	    
	    
	    public List<Booking> findAllBookings() {
	        return bookingRepository.findAll();
	    }

	    // 根據會員 ID 查詢預約
	    public List<Booking> findBookingsByUserId(Integer userId) {
	        return bookingRepository.findByUser_Id(userId);
	    }

	    // 根據預約 ID 查詢單筆預約
	    public Booking findBookingById(Integer bookingId) {
	        return bookingRepository.findById(bookingId)
	                .orElseThrow(() -> new RuntimeException("預約記錄不存在！"));
	    }
	    
	 // 查詢課程排程的所有預約
	    public List<Booking> getBookingsByScheduleId(Integer scheduleId) {
	        return bookingRepository.findBookingsByScheduleId(scheduleId);
	    }
	    
	 //根據會員姓名做模糊查詢   
	    public List<Booking> findBookingsByUserName(String userName) {
	        return bookingRepository.findBookingsByUserName(userName);
	    }
	    
	    public boolean isUserBooked(Integer userId, Integer scheduleId) {
	        return bookingRepository.countExistingBooking(userId, scheduleId) > 0;
	    }
	    
	    public Booking findBookingByUserAndSchedule(Integer userId, Integer scheduleId) {
	        return bookingRepository.findByUser_IdAndSchedule_ScheduleId(userId, scheduleId).orElse(null);
	    }



	    
	 // 刪除預約
	    public void deleteBooking(Integer bookingId) {
	        // 查詢預約記錄
	        Booking booking = bookingRepository.findById(bookingId)
	                .orElseThrow(() -> new RuntimeException("預約記錄不存在，無法刪除！"));

	        // 確保只有 "已預約" 或 "已取消" 狀態才能刪除
	        if (!"已預約".equals(booking.getStatus()) && !"已取消".equals(booking.getStatus())) {
	            throw new RuntimeException("該預約記錄無法刪除！");
	        }

	        // 🔹 取得會員的 UserLevel
	        UserLevel userLevel = userLevelRepository.findByUserId(booking.getUser().getId())
	                .orElseThrow(() -> new RuntimeException("找不到該會員的等級資訊"));

	        // ✅ **退還點數 (如果預約是 "已預約" 狀態)**
	        if ("已預約".equals(booking.getStatus())) {
	            userLevel.setPoints(userLevel.getPoints() + 1);
	            userLevelRepository.save(userLevel);
	        }

	        // ✅ **減少課程人數**
	        Schedule schedule = booking.getSchedule();
	        schedule.setCurrentParticipants(schedule.getCurrentParticipants() - 1);
	        scheduleRepository.save(schedule);

	        // ✅ **刪除預約**
	        bookingRepository.delete(booking);
	    }



	    
	    

	    
	}

