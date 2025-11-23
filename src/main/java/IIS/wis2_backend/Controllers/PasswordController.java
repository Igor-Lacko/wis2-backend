package IIS.wis2_backend.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Request.Auth.PasswordChangeDTO;
import IIS.wis2_backend.DTO.Request.Mail.EmailDTO;
import IIS.wis2_backend.DTO.Request.Mail.PasswordResetDTO;
import IIS.wis2_backend.Services.Account.PasswordService;
import jakarta.validation.Valid;

/**
 * Controller for handling password reset requests.
 */
@RequestMapping("/password")
@RestController
public class PasswordController {
    /**
     * Password reset service.
     */
    private final PasswordService passwordResetService;

    /**
     * Constructor for PasswordResetController.
     * 
     * @param passwordResetService Password reset service.
     */
    public PasswordController(PasswordService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /**
     * POST endpoint for resetting a user's password.
     * 
     * @param passwordResetDTO Contains the token, new password, and password
     *                         confirmation.
     * @return 200 OK if the password was reset successfully.
     */
    @PostMapping
    public ResponseEntity<Void> ResetPassword(@Valid @RequestBody PasswordResetDTO passwordResetDTO) {
        passwordResetService.ResetPassword(passwordResetDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * POST endpoint for generating a password reset link and sending it to the
     * user's email.
     * 
     * @param email The email of the user requesting a password reset.
     * @return 200 OK if the password reset link was generated and sent
     *         successfully.
     */
    @PostMapping("/generate")
    public ResponseEntity<Void> GeneratePasswordResetLink(@Valid @RequestBody EmailDTO emailDTO) {
        passwordResetService.SetupPasswordReset(emailDTO.email());
        return ResponseEntity.ok().build();
    }

    /**
     * POST endpoint for changing a user's password.
     * 
     * @param dto Contains the new password and password confirmation.
     * @return 200 OK if the password was changed successfully.
     */
    @PostMapping("/change")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> ChangePassword(@RequestBody @Valid PasswordChangeDTO dto,
            Authentication authentication) {
        passwordResetService.ChangePassword(authentication.getName(), dto);
        return ResponseEntity.ok().build();
    }

}