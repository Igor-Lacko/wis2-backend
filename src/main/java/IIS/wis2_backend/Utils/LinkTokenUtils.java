package IIS.wis2_backend.Utils;

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
}