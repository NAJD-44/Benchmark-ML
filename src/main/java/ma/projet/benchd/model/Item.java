package ma.projet.benchd.model;

import jakarta.persistence.*; // Important: utilisez jakarta.persistence
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // BIGSERIAL
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "sku", length = 64, nullable = false, unique = true)
    private String sku;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "price", precision = 10, scale = 2, nullable = false) // Correspond à NUMERIC(10,2)
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    private Integer stock; // Correspond à INT

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant updatedAt;

    // --- Relation ---
    // C'est le côté "Many" de la relation
    // Le 'FetchType.LAZY' est crucial pour les performances et demandé
    // Il empêche Hibernate de charger la 'Category' associée à chaque fois que l'on charge un 'Item'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false) // C'est la colonne de la clé étrangère
    private Category category;

    // --- Logique ---
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // --- Getters et Setters ---
    // (À générer avec votre IDE)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}

