import javax.persistence.*;

@Entity
@Table(name = "hierarchy")

public class Hierarchy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    // @Column -> прописываем, если название переменной в классе
    // отличается от названия оного, прописанного в таблице
    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "left_key")
    private Integer leftKey;

    @Column(name = "right_key")
    private Integer rightKey;

    private Integer level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getLeftKey() {
        return leftKey;
    }

    public void setLeftKey(Integer leftKey) {
        this.leftKey = leftKey;
    }

    public Integer getRightKey() {
        return rightKey;
    }

    public void setRightKey(Integer rightKey) {
        this.rightKey = rightKey;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
