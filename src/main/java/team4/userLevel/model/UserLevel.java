package team4.userLevel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import team4.howard.member.model.UserBean;
import team4.membershipLevel.model.MembershipLevel;

@Entity
@Table(name = "UserLevel")
public class UserLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserBean user;

    @ManyToOne
    @JoinColumn(name = "levelId", referencedColumnName = "levelId", nullable = false) // 修正 referencedColumnName
    private MembershipLevel membershipLevel;

    @Column(name = "points", nullable = false)
    private int points; // 當前點數
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    


	

	public UserLevel(Integer id, UserBean user, MembershipLevel membershipLevel, int points, LocalDate startDate,
			LocalDate expiryDate) {
		super();
		this.id = id;
		this.user = user;
		this.membershipLevel = membershipLevel;
		this.points = points;
		this.startDate = startDate;
		this.expiryDate = expiryDate;
	}


	public LocalDate getStartDate() {
		return startDate;
	}


	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}


	public LocalDate getExpiryDate() {
		return expiryDate;
	}


	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}


	public UserLevel() {
    }

    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public MembershipLevel getMembershipLevel() {
        return membershipLevel;
    }

    public void setMembershipLevel(MembershipLevel membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
