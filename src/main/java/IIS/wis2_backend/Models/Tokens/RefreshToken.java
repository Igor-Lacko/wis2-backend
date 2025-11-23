package IIS.wis2_backend.Models.Tokens;

import java.time.Instant;

import IIS.wis2_backend.Models.User.Wis2User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Model for a refresh token.
 * 
 * Inspired by https://medium.com/@victoronu/implementing-refresh-token-logout-in-a-spring-boot-jwt-application-b9d31de953d6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RefreshToken {
    /**
     * Refresh token ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user associated with the token. (Many-to-one, multiple devices can have tokens)
     */
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ToString.Exclude
    private Wis2User user;

    /**
     * The refresh token string.
     */
    @Column(unique = true, nullable = false)
    private String token;

    /**
     * When the refresh token expires.
     */
    @Column(nullable = false)
    private Instant expiryDate;
}