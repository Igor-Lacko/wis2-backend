package IIS.wis2_backend.Utils;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Utility component for handling JWT operations.
 */
@Component
public class JWTUtils {
    /**
     * Secret key for signing JWTs.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT expiration time in milliseconds.
     */
    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    /**
     * JWT issuer.
     */
    @Value("${jwt.issuer}")
    private String jwtIssuer;

    /**
     * Retrieves the encrypted secret key used for signing JWTs.
     *
     * @return the encrypted secret key
     */
    private SecretKey GetSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for the given authentication details.
     *
     * @param authentication the authentication details
     * @return the generated JWT token
     */
    public String generateToken(String username) {
        Date currentDate = new Date(System.currentTimeMillis());

        return Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .issuer(jwtIssuer)
                .signWith(GetSecretKey(), Jwts.SIG.HS256)
                .expiration(new Date(currentDate.getTime() + jwtExpirationMs))
                .compact();
    }

    /**
     * Validates the given JWT token.
     * 
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            // Verify signature
            Claims claims = Parse(token);

            // Verify expiration
            if (claims.getExpiration().before(new Date())) {
                return false;
            }

            // Issuer
            if (!claims.getIssuer().equals(jwtIssuer)) {
                return false;
            }

            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Parses all claims from the given JWT token.
     * 
     * @return the claims contained in the token
     */
    private Claims Parse(String token) {
        return Jwts.parser()
                .verifyWith(GetSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Returns the subject (username) from the given JWT token.
     * 
     * @param token the JWT token
     * @return the subject (username) contained in the token
     */
    public String Subject(String token) {
        return Parse(token).getSubject();
    }
}