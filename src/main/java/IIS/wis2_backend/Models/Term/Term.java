package IIS.wis2_backend.Models.Term;

import jakarta.persistence.*;

/**
 * Base model for all term types.
 */
@Entity
@Table(name = "TERMS")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Term {
    /**
     * Term ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Min amount of points you HAVE to receive for the term. (Well, can be 0)
     */
    Integer minPoints;

    /**
     * Max amount of points you CAN receive for the term.
     */
    Integer maxPoints;

    /**
     * Term description.
     */
    String description;

    /**
     * Term name.
     */
    String name;

    /**
     * Indicates if the term is mandatory.
     */
    Boolean mandatory;
}