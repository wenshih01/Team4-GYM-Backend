package team4.membershipLevel.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipLevelRepository extends JpaRepository<MembershipLevel, Integer> {
	
	 Optional<MembershipLevel> findByLevelName(String levelName);
}

