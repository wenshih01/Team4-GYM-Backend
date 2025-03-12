
package team4.coach.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional

public class CoachService {

	@Autowired
	private CoachRepository cRepo ;
	 
	public Coach findById(String id) {
		Optional<Coach> optional = cRepo.findById(id);
		if ( optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	public List<Coach> findAll(){
		return cRepo.findAll();
	}
	

	
	public Coach insert(Coach coach) {
		return cRepo.save(coach);
	}
	
	public Coach update(Coach coach) {
		return cRepo.save(coach);
	}
	
	public void  deleteById(String id) {
		cRepo.deleteById(id);
	}
	
	public List<Coach> findByEnameContaining(String name) {
	    return cRepo.findByEnameContaining(name);
	}
	
	
}