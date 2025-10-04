package IIS.wis2_backend.Config.Auth;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter to add to the chain for JWT verification.
 */
@Component
public class JWTFilter extends OncePerRequestFilter {
    /**
     * Method to filter each request once.
     */
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) {
        throw new UnsupportedOperationException("Unimplemented method 'doFilterInternal'");
    }
}