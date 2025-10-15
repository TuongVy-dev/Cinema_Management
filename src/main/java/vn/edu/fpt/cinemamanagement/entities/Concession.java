package vn.edu.fpt.cinemamanagement.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Concession")
public class Concession {

    @Id
    @Column(name = "concession_id", length = 255, nullable = false)
    private String concessionId;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    public Concession() {}

    public String getConcessionId() { return concessionId; }
    public void setConcessionId(String concessionId) { this.concessionId = concessionId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Concession that)) return false;
        return Objects.equals(concessionId, that.concessionId);
    }
    @Override public int hashCode() { return Objects.hash(concessionId); }
}
