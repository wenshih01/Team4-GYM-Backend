package team4.booking.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import team4.booking.dto.BookingRequestDTO;
import team4.booking.dto.BookingResponseDTO;
import team4.booking.model.Booking;
import team4.booking.model.BookingService;

@RestController
@RequestMapping("/bookings")
public class BookingController {

	@Autowired
    private BookingService bookingService;
	
	    @GetMapping
	    public List<Booking> getAllBookings() {
	        return bookingService.findAllBookings();
	    }

	    // 根據會員 ID 查詢預約
	    @GetMapping("/user/{userId}")
	    public List<Booking> getBookingsByUserId(@PathVariable Integer userId) {
	        return bookingService.findBookingsByUserId(userId);
	    }

	    // 根據預約 ID 查詢單筆預約
	    @GetMapping("/{bookingId}")
	    public Booking getBookingById(@PathVariable Integer bookingId) {
	        return bookingService.findBookingById(bookingId);
	    }
	    
	    // 根據課程排程 ID 查詢所有預約
	    @GetMapping("/schedule/{scheduleId}")
	    public List<Booking> getBookingsByScheduleId(@PathVariable Integer scheduleId) {
	        return bookingService.getBookingsByScheduleId(scheduleId);
	    }
	    
	    @GetMapping("/search")
	    public List<Booking> searchBookingsByUserName(@RequestParam String name) {
	        return bookingService.findBookingsByUserName(name);
	    }
	
	


	 // 新增預約
	    @PostMapping
	    public BookingResponseDTO createBooking(@RequestBody BookingRequestDTO requestDTO) {
	        Booking booking = bookingService.createBooking(requestDTO.getUserId(), requestDTO.getScheduleId());
	        return mapToResponseDTO(booking);
	    }

	    // 將 Booking 物件映射到 ResponseDTO
	    private BookingResponseDTO mapToResponseDTO(Booking booking) {
	        BookingResponseDTO responseDTO = new BookingResponseDTO();
	        responseDTO.setBookingId(booking.getBookingId());
	        responseDTO.setUserId(booking.getUser().getId());
	        responseDTO.setUsername(booking.getUser().getUsername());
	        responseDTO.setScheduleId(booking.getSchedule().getScheduleId());
	        responseDTO.setBookingTime(booking.getBookingTime());
	        responseDTO.setStatus(booking.getStatus());
	        return responseDTO;
	    }
	

    // 取消預約
	    @PutMapping("/{bookingId}/cancel")
	    public String cancelBooking(@PathVariable Integer bookingId) {
	        bookingService.cancelBooking(bookingId);
	        return "預約已取消，點數已退回！";
	    }

	    @PutMapping("/{bookingId}/restore")
	    public String restoreBooking(@PathVariable Integer bookingId) {
	        bookingService.restoreBooking(bookingId);
	        return "預約已成功恢復！";
	    }
	
    @DeleteMapping("/{bookingId}")
    public String deleteBooking(@PathVariable Integer bookingId) {
        bookingService.deleteBooking(bookingId);
        return "預約記錄已成功刪除，點數已退回！";
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    
    @GetMapping("/user/{userId}/schedule/{scheduleId}")
    public ResponseEntity<?> checkIfUserBooked(@PathVariable Integer userId, @PathVariable Integer scheduleId) {
        Booking booking = bookingService.findBookingByUserAndSchedule(userId, scheduleId);
        if (booking != null) {
            return ResponseEntity.ok(Map.of("isBooked", true, "bookingId", booking.getBookingId()));
        } else {
            return ResponseEntity.ok(Map.of("isBooked", false));
        }
    }


	
    
}
