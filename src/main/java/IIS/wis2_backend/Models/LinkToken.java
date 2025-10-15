package IIS.wis2_backend.Models;

import java.sql.Date;

import IIS.wis2_backend.Enum.LinkTokenType;
import IIS.wis2_backend.Models.User.Wis2User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for a account activation or password reset token.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LinkToken {
    /**
     * The user ID (also the token ID).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The hash of the token sent to the user.
     */
    private String tokenHash;

    /**
     * The user associated with the token.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private Wis2User user;

    /**
     * The time the token expires at.
     */
    private Date expirationDate;

    /**
     * Type of the token (activation or password reset).
     */
    @Enumerated(EnumType.STRING)
    private LinkTokenType type;
}