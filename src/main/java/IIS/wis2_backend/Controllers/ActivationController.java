package IIS.wis2_backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.Services.AccountActivationService;

/**
 * Controller for handling account activation related requests (well, the one request).
 */
@RestController
@RequestMapping("/activate")
public class ActivationController {
    /**
     * The service for account activation.
     */
    private final AccountActivationService accountActivationService;

    /**
     * Constructor for ActivationController.
     * 
     * @param accountActivationService The service for account activation.
     */
    @Autowired
    public ActivationController(AccountActivationService accountActivationService) {
        this.accountActivationService = accountActivationService;
    }

    /**
     * Endpoint to activate an account.
     * 
     * @param token Query parameter containing the activation token.
     * @return ResponseEntity with status 200 if the account was activated successfully.
     */
    @GetMapping
    public ResponseEntity<Void> ActivateAccount(String token) {
        accountActivationService.ActivateAccount(token);
        return ResponseEntity.ok().build();
    }
}