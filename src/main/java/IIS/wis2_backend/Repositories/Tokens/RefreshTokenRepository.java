package IIS.wis2_backend.Repositories.Tokens;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.Tokens.RefreshToken;

/**
 * Repository for managing refresh tokens.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    /**
     * Finds a refresh token by its token string.
     * 
     * @param token The token string.
     * @return The refresh token if found, otherwise null.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Deletes all expired refresh tokens before the given instant.
     * 
     * @param now The instant to compare expiration against.
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    void deleteAllExpiredTokens(@Param("now") Instant now);
}