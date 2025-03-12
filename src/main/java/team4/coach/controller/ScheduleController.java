
package team4.coach.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import team4.coach.model.Coach;
import team4.coach.model.CoachRepository;
import team4.coach.model.Course;
import team4.coach.model.CourseRepository;
import team4.coach.model.Schedule;
import team4.coach.model.ScheduleRepository;
import team4.coach.model.ScheduleRequest;
import team4.coach.model.ScheduleService;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/schedules")
@CrossOrigin
public class ScheduleController {

   @Autowired
   private ScheduleService scheduleService;
   
   
   
   @Autowired
   private CoachRepository coachRepository;
   
   @Autowired 
   private CourseRepository courseRepository;

   @GetMapping
   public ResponseEntity<List<Schedule>> getAll(@RequestParam(required = false) String courseName) {
       if (courseName != null && !courseName.trim().isEmpty()) {
           List<Schedule> schedules = scheduleService.findByCourseName(courseName);
           return ResponseEntity.ok(schedules);
       } else {
           List<Schedule> schedules = scheduleService.findAll();
           return ResponseEntity.ok(schedules);
       }
   }

   @GetMapping("/{id}")
   public ResponseEntity<Schedule> getById(@PathVariable Integer id) {
       Schedule s = scheduleService.findById(id);
       if (s == null) {
           return ResponseEntity.notFound().build();
       }
       return ResponseEntity.ok(s);
   }

   @PostMapping
   public ResponseEntity<Schedule> createSchedule(@RequestBody @Validated ScheduleRequest scheduleRequest) {
       Schedule schedule = scheduleService.createSchedule(scheduleRequest);
       return ResponseEntity.ok(schedule);
   }

   @PutMapping("/{id}")
   public ResponseEntity<Schedule> updateSchedule(
           @PathVariable("id") Integer scheduleId,
           @RequestBody ScheduleRequest scheduleRequest
   ) {
       Schedule updatedSchedule = scheduleService.updateSchedule(scheduleId, scheduleRequest);
       return ResponseEntity.ok(updatedSchedule);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> delete(@PathVariable Integer id) {
       scheduleService.deleteSchedule(id);
       return ResponseEntity.noContent().build();
   }

   @GetMapping("/coaches")
   public ResponseEntity<List<Coach>> getAllCoaches() {
       return ResponseEntity.ok(coachRepository.findAll());
   }
   
   @GetMapping("/courses")
   public ResponseEntity<List<Course>> getAllCourses() {
       return ResponseEntity.ok(courseRepository.findAll());
   }

   @PostMapping("/recurring")
   public ResponseEntity<List<Schedule>> createRecurringSchedules(
           @RequestBody @Validated ScheduleRequest scheduleRequest,
           @RequestParam("startDate") String startDate, 
           @RequestParam("endDate") String endDate) {
       LocalDate start = LocalDate.parse(startDate);
       LocalDate end = LocalDate.parse(endDate);
       List<Schedule> schedules = scheduleService.createRecurringSchedules(scheduleRequest, start, end);
       return ResponseEntity.ok(schedules);
   }

   @PutMapping("/recurring/{id}")
   public ResponseEntity<List<Schedule>> updateRecurringSchedules(
           @PathVariable("id") Integer scheduleId,
           @RequestBody @Validated ScheduleRequest scheduleRequest,
           @RequestParam("startDate") String startDate,
           @RequestParam("endDate") String endDate) {
       LocalDate start = LocalDate.parse(startDate);
       LocalDate end = LocalDate.parse(endDate); 
       List<Schedule> schedules = scheduleService.createOrUpdateRecurringSchedules(scheduleRequest, start, end, scheduleId);
       return ResponseEntity.ok(schedules);
   }


   @PutMapping("/cancel/{scheduleId}")
    public String cancelSchedule(@PathVariable Integer scheduleId) {
        scheduleService.cancelSchedule(scheduleId); 
        return "課程已成功取消";
    }
    
    @PutMapping("/restore/{scheduleId}")
    public String restoreSchedule(@PathVariable Integer scheduleId) {
        scheduleService.restoreSchedule(scheduleId);
        return "課程已成功恢復";
    }
    


}