package IIS.wis2_backend.Models;

import jakarta.persistence.*;

/**
 * Model representing a course unit credit.
 * @todo This class is one big todo.
 */
@Entity
@Table(name = "UNIT_CREDITS")
public class UnitCredit {
    /**
     * Unit credit ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * If the unit credit is automatic.
     */
    private Boolean automatic;

    protected UnitCredit() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAutomatic() {
        return automatic;
    }

    public void setAutomatic(Boolean automatic) {
        this.automatic = automatic;
    }
}