package team4.coach.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	//http://localhost:8081/home.controller
	
	@GetMapping("/home.controller")
	public String processAction() {
		return "home";
	}

}