package IIS.wis2_backend.Utils;

import org.springframework.beans.factory.annotation.Value;

/**
 * Utility class for handling JWT operations.
 */
public class JWTUtils {
    /**
     * Secret key for signing JWTs.
     */
    @Value("${jwt.secret}")
    private String SECRET_KEY;
}