package ma.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*; // Important: utilisez jakarta.persistence, pas javax.persistence
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Correspond à BIGSERIAL
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", length = 32, nullable = false, unique = true)
    private String code;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP) // Type TIMESTAMP SQL
    private Instant updatedAt; // 'Instant' est le type Java moderne pour TIMESTAMP

    // --- Relation ---
    // C'est le côté "One" de la relation One-to-Many
    // "mappedBy" indique que c'est l'entité 'Item' qui gère la clé étrangère (via son champ "category")
    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Item> items;

    // --- Logique (ex: mettre à jour 'updated_at' avant de sauvegarder) ---
    @PrePersist // Avant une NOUVELLE sauvegarde
    @PreUpdate  // Avant une MISE À JOUR
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // --- Getters et Setters ---
    // (Vous pouvez les générer automatiquement avec votre IDE)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
