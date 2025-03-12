package team4.membershipLevel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MembershipLevel")
public class MembershipLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "levelId")
    private Integer levelId; // 建議改成 Integer 避免 Hibernate Null Pointer 問題

    @Column(name = "levelName", unique = true, nullable = false)
    private String levelName;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "maxPoints", nullable = false)
    private int maxPoints;
    
    @Column(name = "durationMonths", nullable = false) // 新增會員等級的有效月份
    private int durationMonths;

    public int getDurationMonths() {
		return durationMonths;
	}

	public void setDurationMonths(int durationMonths) {
		this.durationMonths = durationMonths;
	}

	public MembershipLevel() {}

    

    public MembershipLevel(Integer levelId, String levelName, double price, int maxPoints, int durationMonths) {
		super();
		this.levelId = levelId;
		this.levelName = levelName;
		this.price = price;
		this.maxPoints = maxPoints;
		this.durationMonths = durationMonths;
	}

	public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }
}
