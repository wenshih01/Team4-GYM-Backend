package team4.coach.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Integer> {
	
	// 使用 Spring Data JPA 提供的命名規則，支援模糊查詢 (Containing)
    List<Course> findByCourseNameContaining(String keyword);

}