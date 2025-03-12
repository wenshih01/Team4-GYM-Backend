package team4.coach.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachRepository extends JpaRepository<Coach, String> {

	List<Coach> findByEnameContaining(String ename);
}