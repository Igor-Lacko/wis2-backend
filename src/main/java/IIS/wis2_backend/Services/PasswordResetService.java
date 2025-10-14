package IIS.wis2_backend.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.Repositories.LinkTokenRepository;

/**
 * Service for handling password reset functionality.
 */
@Service
public class PasswordResetService {
    /**
     * Base URL for password reset links prepended to /reset-password?token=....
     */
    @Value("${server.url}")
    private String serverUrl;

    /**
     * The expiration time for password reset links in minutes.
     */
    @Value("${password.reset.link.expiration.minutes}")
    private int expirationTimeInMinutes;

    /**
     * Repository for password reset tokens.
     */
    private final LinkTokenRepository passwordResetTokenRepository;

    /**
     * Constructor for PasswordResetService.
     * 
     * @param passwordResetTokenRepository Repository for password reset tokens.
     */
    @Autowired
    public PasswordResetService(LinkTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }
}