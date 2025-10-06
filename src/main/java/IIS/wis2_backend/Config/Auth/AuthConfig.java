package IIS.wis2_backend.Config.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import IIS.wis2_backend.JWTUtils;
import IIS.wis2_backend.Repositories.User.UserRepository;
import IIS.wis2_backend.Services.AuthService;

/**
 * Configuration class for authentication settings.
 */
@Configuration
public class AuthConfig {
    /**
     * List of public endpoints. They will be added here as the development goes on.
     */
    private static final String[] PUBLIC_ENDPOINTS = {
        "/auth/login",
        "/auth/register",
        "/home",
        "/"
    };

    /**
     * Auth service for UserDetailsService for AuthenticationProvider.
     */
    private final AuthService authService;

    /**
     * Entry point for unauthorized requests.
     */
    private final AuthenticationEntryPoint unauthorizedHandler;

    /**
     * JWT Utils for the filter.
     */
    private final JWTUtils jwtUtils;

    /**
     * Constructor for AuthConfig.
     * 
     * @param authService The authentication service.
     */
    @Autowired
    public AuthConfig(AuthService authService, AuthenticationEntryPoint unauthorizedHandler, UserRepository userRepository, JWTUtils jwtUtils) {
        this.authService = authService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Bean for password encoding.
     * 
     * @return BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Bean for security filter chain.
     * 
     * @return SecurityFilterChain instance.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(AuthConfig.PUBLIC_ENDPOINTS)
                    .permitAll()
                .anyRequest()
                    .authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
            .addFilterBefore(
                authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /**
     * Authentication provider bean.
     * 
     * @return Custom authentication provider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(authService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * JWT filter bean.
     * 
     * @return JWTFilter instance.
     */
    @Bean
    public OncePerRequestFilter authenticationJwtTokenFilter() {
        return new JWTFilter(jwtUtils, authService);
    }

    /**
     * Authentication manager bean.
     * 
     * @return AuthenticationManager instance.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}