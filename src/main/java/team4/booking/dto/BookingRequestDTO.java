package team4.booking.dto;

public class BookingRequestDTO {

	private Integer userId;      // 會員 ID
    private Integer scheduleId;  // 課程排程 ID

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }
}

