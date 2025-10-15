package IIS.wis2_backend.Utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for link token generation.
 */
public class LinkTokenUtils {
    /**
     * URL encoder instance.
     */
    public static final Base64.Encoder urlEncoder = Base64.getUrlEncoder();

    /**
     * Secure random instance.
     */
    public static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a secure random token encoded in URL-safe Base64.
     * 
     * @param byteLength The length of the random byte array to generate before encoding.
     * @return A URL-safe Base64 encoded token string.
     */
    public static String GenerateLinkToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return urlEncoder.encodeToString(randomBytes);
    }

    /**
     * Hashes the given token using SHA-256 and encodes the result in URL-safe Base64.
     * 
     * @param token The token to hash.
     * @return The SHA-256 hash of the token, encoded in URL-safe Base64.
     */
    public static String HashToken(String token) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(token.getBytes(StandardCharsets.UTF_8));
            return urlEncoder.encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // This should NOT happen
            throw new IllegalStateException("SHA-256 algorithm not found", e);
        }
    }
}