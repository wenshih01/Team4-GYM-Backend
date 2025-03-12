package team4.booking.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface BookingRepository extends JpaRepository<Booking, Integer> {
	
	 List<Booking> findByUser_Id(Integer userId);
    // 可以在這裡新增自訂查詢方法，例如根據會員查詢預約
	 
	 // 根據課程排程 ID 查詢預約
	 @Query("SELECT b FROM Booking b WHERE b.schedule.scheduleId = :scheduleId")
	 List<Booking> findBookingsByScheduleId(@Param("scheduleId") Integer scheduleId);

	 
	 @Query("SELECT COUNT(b) FROM Booking b WHERE b.schedule.scheduleId = :scheduleId AND b.status = '已預約'")
	 int countBySchedule(@Param("scheduleId") Integer scheduleId);
	 
	 @Query("SELECT b FROM Booking b WHERE b.user.name LIKE %:name%")
	 List<Booking> findBookingsByUserName(@Param("name") String name);
	 
	 boolean existsByUser_IdAndSchedule_ScheduleId(Integer userId, Integer scheduleId);
	 
	 @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.schedule.scheduleId = :scheduleId AND b.status = '已預約'")
	 Long countExistingBooking(@Param("userId") Integer userId, @Param("scheduleId") Integer scheduleId);


	 Optional<Booking> findByUser_IdAndSchedule_ScheduleId(Integer userId, Integer scheduleId);



}