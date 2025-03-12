
package team4.coach.model;

import java.time.LocalDateTime;



public class ScheduleRequest {

	   
	    private Integer courseID;
	   // 只需要 courseId
	    private String empno; 
	    // 只需要 coach 的 empno
	    private LocalDateTime startTime;
	 
	    private LocalDateTime endTime;
	 
	    private String status;
	    
	    private int maxParticipants; 
	    
		public Integer getCourseID() {
			return courseID;
		}
		public void setCourseID(Integer courseID) {
			this.courseID = courseID;
		}
		public int getMaxParticipants() {
			return maxParticipants;
		}
		public void setMaxParticipants(int maxParticipants) {
			this.maxParticipants = maxParticipants;
		}
		public Integer getCourseId() {
			return courseID;
		}
		public void setCourseId(Integer courseId) {
			this.courseID = courseId;
		}
		public String getEmpno() {
			return empno;
		}
		public void setEmpno(String empno) {
			this.empno = empno;
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

	    // Getter & Setter
	}

