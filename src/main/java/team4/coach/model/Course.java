
package team4.coach.model;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Course")
@Component
public class Course {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name = "courseId") 
	private Integer courseID;
	
	
	
	@Column(name = "courseName" ,nullable = false, length = 100)
    private String courseName;
	@Column(name = "description",length = 255 )
    private String description;
	@Column(name = "durationMinutes",nullable = false)
    private Integer durationMinutes;
	@Column(name = "createdAt",updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
	@Column(name = "updatedAt")
	private LocalDateTime updatedAt = LocalDateTime.now();
	
	public Integer getCourseID() {
		return courseID;
	}
	public void setCourseID(Integer courseID) {
		this.courseID = courseID;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getDurationMinutes() {
		return durationMinutes;
	}
	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	public Course(Integer courseID, String courseName, String description, Integer durationMinutes,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.courseID = courseID;
		this.courseName = courseName;
		this.description = description;
		this.durationMinutes = durationMinutes;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	public Course() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}