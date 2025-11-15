package IIS.wis2_backend.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Enum.LinkTokenType;
import IIS.wis2_backend.Models.LinkToken;

/**
 * Repository for activation and password reset tokens.
 */
@Repository
public interface LinkTokenRepository extends JpaRepository<LinkToken, Long> {
    /**
     * Finds a link token by its token (the actual string) and type.
     * 
     * @param token The hashed token itself.
     * @param type  The type of the token (activation or password reset).
     * @return The LinkToken with the given token, or null if not found.
     */
    Optional<LinkToken> findByTokenHashAndType(String tokenHash, LinkTokenType type);
}