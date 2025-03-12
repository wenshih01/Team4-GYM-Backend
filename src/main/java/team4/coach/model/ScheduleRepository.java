
package team4.coach.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import team4.booking.model.Booking;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    // 先前已用於新增排程前的時間重疊檢查 (不排除自己):
    @Query("""
        SELECT s
        FROM Schedule s
        WHERE s.coach.empno = :empNo
          AND (s.startTime < :end AND s.endTime > :start)
    """)
    List<Schedule> findOverlappingByCoach(
            @Param("empNo") String empNo,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT s
        FROM Schedule s
        WHERE s.course.courseID = :courseId
          AND (s.startTime < :end AND s.endTime > :start)
    """)
    List<Schedule> findOverlappingByCourse(
            @Param("courseId") Integer courseId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    
    // -------------------------
    // 針對更新時排除自己這筆紀錄 (exclude self)
    // -------------------------
    @Query("""
        SELECT s
        FROM Schedule s
        WHERE s.coach.empno = :empNo
          AND s.scheduleId <> :selfScheduleId
          AND (s.startTime < :end AND s.endTime > :start)
    """)
    List<Schedule> findOverlappingByCoachExcludeSelf(
            @Param("empNo") String empNo,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("selfScheduleId") Integer selfScheduleId
    );

    @Query("""
        SELECT s
        FROM Schedule s
        WHERE s.course.courseID = :courseId
          AND s.scheduleId <> :selfScheduleId
          AND (s.startTime < :end AND s.endTime > :start)
    """)
    List<Schedule> findOverlappingByCourseExcludeSelf(
            @Param("courseId") Integer courseId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("selfScheduleId") Integer selfScheduleId
    );
    
    @Query("""
    	    SELECT s
    	    FROM Schedule s
    	    WHERE s.course.courseName LIKE %:courseName%
    	""")
    	List<Schedule> findByCourseNameContaining(@Param("courseName") String courseName);

 
       //從教練查詢課程方法
       List<Schedule> findByCoachEmpno(String empno);
       
       @Query("SELECT COUNT(b) FROM Booking b WHERE b.schedule.scheduleId = :scheduleId AND b.status = '已預約'")
       int countByScheduleId(@Param("scheduleId") Integer scheduleId);

       List<Booking> findBookingsByScheduleId(Integer scheduleId);
}
