package IIS.wis2_backend.Models;

import java.sql.Date;

import IIS.wis2_backend.Models.User.Wis2User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
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
     * The user ID (also the token ID).
     */
    @Id
    private Long id;
    /**
     * The token itself.
     */
    private String token;

    /**
     * The user associated with the token.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private Wis2User user;

    /**
     * The time the token was issued at.
     */
    private Date issuedAt;
}