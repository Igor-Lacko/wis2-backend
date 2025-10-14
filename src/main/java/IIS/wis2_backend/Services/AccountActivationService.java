package IIS.wis2_backend.Services;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.Exceptions.ExceptionTypes.ActivationException;
import IIS.wis2_backend.Models.ActivationToken;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.ActivationTokenRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;

import java.security.SecureRandom;
import java.sql.Date;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Service for account activation.
 */
@Service
public class AccountActivationService {
    /**
     * The expiration time for activation links in hours.
     */
    @Value("${ACTIVATION_LINK_EXPIRATION_HOURS}")
    private int activationLinkExpirationHours;

    /**
     * Repository for activation tokens.
     */
    private final ActivationTokenRepository activationTokenRepository;

    /**
     * User repository to set isActive to true.
     */
    private final UserRepository userRepository;

    /**
     * URL encoder instance.
     */
    private static final Base64.Encoder urlEncoder = Base64.getUrlEncoder();

    /**
     * Secure random instance.
     */
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Base URL for activation links.
     */
    private static final String baseUrl = "/activate";

    /**
     * Constructor for AccountActivationService.
     * 
     * @param activationTokenRepository Repository for activation tokens.
     */
    @Autowired
    public AccountActivationService(ActivationTokenRepository activationTokenRepository,
            UserRepository userRepository) {
        this.activationTokenRepository = activationTokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Try to activate an account with the given token.
     * 
     * @param token The token passed in by the user.
     * 
     * @throws ActivationException if the token is invalid or expired.
     */
    public void ActivateAccount(String token) {
        // Try to find the token in the database
        var activationToken = activationTokenRepository.findByToken(token);
        if (activationToken == null) {
            throw new ActivationException("Invalid activation token.");
        }

        // Check if the token is expired
        var issuedAt = activationToken.getIssuedAt();
        var now = new java.util.Date();
        var diffInMillies = now.getTime() - issuedAt.getTime();
        var diffInHours = diffInMillies / (1000 * 60 * 60);
        if (diffInHours > activationLinkExpirationHours) {
            throw new ActivationException("Activation token has expired.");
        }

        // Activate the user
        Wis2User user = activationToken.getUser();
        if (user.isActivated()) {
            throw new ActivationException("Account is already activated.");
        }

        user.setActivated(true);
        userRepository.save(user);
    }

    /**
     * Creates a new activation token for the given user and associates them. Called
     * after registering.
     * 
     * @param userId the ID of the registered user.
     * @return The generated token.
     */
    public String OnRegister(Long userId) {
        String token = GenerateActivationToken();

        // Associate the token with the user
        var user = userRepository.findById(userId).orElseThrow();
        ActivationToken activationToken = ActivationToken.builder()
                .token(token)
                .user(user)
                .issuedAt(new Date(System.currentTimeMillis()))
                .build();

        activationTokenRepository.save(activationToken);
        return token;
    }

    /**
     * Generates a random 256-bit token.
     * 
     * @return The generated token.
     */
    public String GenerateActivationToken() {
        byte[] randBytes = new byte[32];
        secureRandom.nextBytes(randBytes);
        return urlEncoder.encodeToString(randBytes);
    }

    /**
     * Generates an activation link with the given token. Assumes the token is
     * already Base64 URL encoded.
     * 
     * @param token The activation token.
     * @return The activation link.
     */
    public String GenerateActivationLink(String token) {
        return baseUrl + "?token=" + token;
    }
}
