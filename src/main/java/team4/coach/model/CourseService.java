
package team4.coach.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    // 單筆查詢
    public Course findById(Integer courseID) {
        Optional<Course> optional = courseRepository.findById(courseID);
        return optional.orElse(null);
    }

    // 多筆查詢
    public List<Course> findAll() {
        return courseRepository.findAll();
    }
    
 // 模糊查詢: 根據課程名稱關鍵字
    public List<Course> searchByCourseName(String keyword) {
        return courseRepository.findByCourseNameContaining(keyword);
    }

    // 新增課程
    public Course insert(Course course) {
        return courseRepository.save(course);
    }

    // 修改課程
    public Course update(Course course) {
        return courseRepository.save(course);
    }

    // 刪除課程（無回傳值）
    public void deleteById(Integer courseID) {
        courseRepository.deleteById(courseID);
    }
}