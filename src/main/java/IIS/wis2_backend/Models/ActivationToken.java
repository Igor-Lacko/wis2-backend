package IIS.wis2_backend.Models;

import java.sql.Date;

import IIS.wis2_backend.Models.User.Wis2User;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for a account activation token.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ActivationToken {
    /**
     * The token itself.
     */
    private String token;

    /**
     * The user associated with the token.
     */
    @OneToOne
    private Wis2User user;

    /**
     * The time the token was issued at.
     */
    private Date issuedAt;
}