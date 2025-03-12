package team4.coach.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import team4.coach.model.Coach;
import team4.coach.model.CoachService;


	@Controller
	public class testController {

	    @Autowired
	    private CoachService coachService;

	    @RequestMapping("/coach/{empno}")
	    public String getCoachByEmpno(@PathVariable String empno, Model model) {
	        Coach coach = coachService.findById(empno);
	        if (coach != null) {
	            model.addAttribute("coach", coach);
	        } else {
	            model.addAttribute("message", "找不到教練資料！");
	        }
	        return "/Coaches.jsp";
	    }
	}
