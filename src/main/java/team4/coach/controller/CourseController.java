
package team4.coach.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import team4.coach.model.Course;
import team4.coach.model.CourseService;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * [GET] http://localhost:8080/courses/getAll
     * 查詢全部課程並返回 JSON 格式
     */
    @GetMapping("/getAll")
    public List<Course> getAllCourses() {
        return courseService.findAll();
    }

    /**
     * [GET] http://localhost:8080/courses/getCourse/{id}
     * 根據課程 ID 查詢單筆課程
     */
    @GetMapping("/getCourse/{id}")
    public Course getCourseById(@PathVariable Integer id) {
        return courseService.findById(id);
    }

    /**
     * [POST] http://localhost:8080/courses/addCourse
     * 新增一筆課程
     */
    @PostMapping("/addCourse")
    public Course addCourse(@RequestBody Course course) {
        return courseService.insert(course);
    }

    /**
     * [PUT] http://localhost:8080/courses/updateCourse/{id}
     * 更新一筆課程
     */
    @PutMapping("/updateCourse/{id}")
    public Course updateCourse(@PathVariable Integer id, @RequestBody Course updatedCourse) {
        Course existingCourse = courseService.findById(id);
        if (existingCourse != null) {
            existingCourse.setCourseName(updatedCourse.getCourseName());
            existingCourse.setDescription(updatedCourse.getDescription());
            existingCourse.setDurationMinutes(updatedCourse.getDurationMinutes());
            return courseService.update(existingCourse);
        }
        return null;  // 或者可以返回一個錯誤訊息
    }

    /**
     * [DELETE] http://localhost:8080/courses/deleteCourse/{id}
     * 根據課程 ID 刪除一筆課程
     */
    @DeleteMapping("/deleteCourse/{id}")
    public String deleteCourse(@PathVariable Integer id) {
        Course existingCourse = courseService.findById(id);
        if (existingCourse != null) {
            courseService.deleteById(id);
            return "課程已成功刪除";
        }
        return "找不到指定的課程";
    }
    
    @GetMapping("/search")
    public List<Course> searchCourses(@RequestParam("keyword") String keyword) {
        return courseService.searchByCourseName(keyword);
    }
}