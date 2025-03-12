
package team4.coach.model;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Schedule")
@Component
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;

    /**
     * 多對一：一門課 (Course) 可能對應多筆排程
     */
    
    @ManyToOne
    @JoinColumn(name = "courseid", referencedColumnName = "courseid", nullable = false)
    private Course course;

    /**
     * 多對一：一位教練 (Employee) 可能對應多筆排程
     */
    @ManyToOne
    @JoinColumn(name = "empno", referencedColumnName = "empno", nullable = false)
    private Coach coach;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "course_status", length = 50, nullable = false)
    private String status;
    
    @Column(name = "manual_status", length = 50)
    private String manualStatus; // 手動設置的狀態：canceled, active, null（表示未手動設置）

    @Column(name = "max_participants", nullable = false)
    private int maxParticipants = 10; // 設定預設最大報名人數為 10
    
    @Column(name = "current_participants", nullable = false)
    private int currentParticipants = 0; // 新增「報名人數」欄位，預設 0 人

	

	public int getCurrentParticipants() {
		return currentParticipants;
	}

	public void setCurrentParticipants(int currentParticipants) {
		this.currentParticipants = currentParticipants;
	}

	

	public Schedule(Integer scheduleId, Course course, Coach coach, LocalDateTime startTime, LocalDateTime endTime,
			String status, String manualStatus, int maxParticipants, int currentParticipants) {
		super();
		this.scheduleId = scheduleId;
		this.course = course;
		this.coach = coach;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.manualStatus = manualStatus;
		this.maxParticipants = maxParticipants;
		this.currentParticipants = currentParticipants;
	}

	public int getMaxParticipants() {
		return maxParticipants;
	}

	public void setMaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}

	public String getManualStatus() {
		return manualStatus;
	}

	public void setManualStatus(String manualStatus) {
		this.manualStatus = manualStatus;
	}

	public Integer getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Coach getCoach() {
		return coach;
	}

	public void setCoach(Coach coach) {
		this.coach = coach;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	

	public Schedule() {
		super();
		
	}

}