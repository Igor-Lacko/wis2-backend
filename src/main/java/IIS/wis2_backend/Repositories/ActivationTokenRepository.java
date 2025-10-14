package IIS.wis2_backend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.ActivationToken;

/**
 * Repository for activation tokens.
 */
@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, String> {
    /**
     * Finds an activation token by its token.
     * 
     * @param token The token itself.
     * @return The ActivationToken with the given token, or null if not found.
     */
    ActivationToken findByToken(String token);
}