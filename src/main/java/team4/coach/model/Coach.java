
package team4.coach.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "EMPLOYEE")
@Component
public class Coach {

	@Id
	@Column(name = "empno")
	private String empno;
	@Column(name = "ename")
	private String ename;
	@Column(name = "nickname")
	private String nickname;
	@Column(name = "salary")
	private String salary;
	@Column(name = "hiredate")
	private String hiredate;
	@Column(name = "title")
	private String title;
	@Column(name = "skill")
	private String skill;
	@Column(name = "photo")
	private String photo;
	@Column(name = "experience")
	private String experience;
	
	public Coach() {
		
	}

	public Coach(String empno, String ename, String nickname, String salary, String hiredate, String title,
			String skill, String photo, String experience) {
		super();
		this.empno = empno;
		this.ename = ename;
		this.nickname = nickname;
		this.salary = salary;
		this.hiredate = hiredate;
		this.title = title;
		this.skill = skill;
		this.photo = photo;
		this.experience = experience;
	}

	public String getEmpno() {
		return empno;
	}

	public void setEmpno(String empno) {
		this.empno = empno;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getHiredate() {
		return hiredate;
	}

	public void setHiredate(String hiredate) {
		this.hiredate = hiredate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}
	
}