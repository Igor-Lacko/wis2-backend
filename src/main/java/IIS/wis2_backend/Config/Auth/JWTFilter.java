package IIS.wis2_backend.Config.Auth;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import IIS.wis2_backend.Services.Wis2UserDetailsService;
import IIS.wis2_backend.Utils.JWTUtils;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter to add to the chain for JWT verification.
 */
public class JWTFilter extends OncePerRequestFilter {
    /**
     * Class to filter JWTs from requests.
     */
    private final JWTUtils jwtUtils;

    /**
     * Service to load user details.
     */
    private final Wis2UserDetailsService wis2UserDetailsService;

    /**
     * Constructor for JWTFilter.
     *
     * @param jwtUtils    the JWTUtils instance
     * @param authService the AuthService instance
     */
    public JWTFilter(JWTUtils jwtUtils, Wis2UserDetailsService wis2UserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.wis2UserDetailsService = wis2UserDetailsService;
    }

    /**
     * Method to filter each request once.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Also skip public endpoints, TODO maybe remove
        String path = request.getServletPath();
        for (String endpoint : AuthConfig.PUBLIC_ENDPOINTS) {
            if (path.startsWith(endpoint)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String token = authHeader.substring("Bearer ".length());

        if (!jwtUtils.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = jwtUtils.Subject(token);
        try {
            UserDetails userDetails = wis2UserDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (UsernameNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}