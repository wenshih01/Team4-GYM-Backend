package team4.train.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
//123
@Entity
@Data
@Table(name = "ACTIONS")
@NoArgsConstructor
@AllArgsConstructor
public class TrainBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "parts", nullable = false)
    private String parts;

    @Column(name = "teaching")
    private String teaching;

    @Column(name = "url")
    private String url;

    @Column(name = "calories", nullable = false)
    private int calories;

    // 新增 imageUrl 欄位，允許為 null
    @Column(name = "imageUrl", nullable = true)
    private String imageUrl;

    @OneToMany(mappedBy = "trainBean", cascade = CascadeType.ALL)
    @JsonManagedReference("trainBeanReference")
    private List<PlanDetail> planDetails;

    // 可視需要自行加上包含 imageUrl 的建構子
    public TrainBean(int id, String name, String parts, String teaching, String url, int calories) {
        super();
        this.id = id;
        this.name = name;
        this.parts = parts;
        this.teaching = teaching;
        this.url = url;
        this.calories = calories;
    }

    public TrainBean(int id, String name, String parts, String teaching, String url, int calories, String imageUrl) {
        super();
        this.id = id;
        this.name = name;
        this.parts = parts;
        this.teaching = teaching;
        this.url = url;
        this.calories = calories;
        this.imageUrl = imageUrl;
    }
}
