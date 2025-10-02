package IIS.wis2_backend.Models;

import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing a course unit credit.
 * @todo This class is one big todo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
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
}