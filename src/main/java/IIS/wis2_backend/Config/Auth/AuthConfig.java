package IIS.wis2_backend.Config.Auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;

import IIS.wis2_backend.Repositories.User.UserRepository;
import IIS.wis2_backend.Services.Wis2UserDetailsService;
import IIS.wis2_backend.Utils.JWTUtils;

/**
 * Configuration class for authentication settings.
 */
@Configuration
public class AuthConfig {
    /**
     * List of public endpoints. They will be added here as the development goes on.
     */
    public static final String[] PUBLIC_ENDPOINTS = {
            "/auth/login",
            "/auth/register",
            "/activate",
            "/courses",
            "/password-reset",
            "/password-reset/generate",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v8/api-docs/**",
            "/v8/**"
    };

    /**
     * Service for user details.
     */
    private final Wis2UserDetailsService wis2UserDetailsService;

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
    public AuthConfig(Wis2UserDetailsService wis2UserDetailsService, AuthenticationEntryPoint unauthorizedHandler,
            UserRepository userRepository, JWTUtils jwtUtils) {
        this.wis2UserDetailsService = wis2UserDetailsService;
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
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("http://localhost:5173", "//TODO"));
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AuthConfig.PUBLIC_ENDPOINTS)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .userDetailsService(wis2UserDetailsService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                .addFilterBefore(
                        authenticationJwtTokenFilter(),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * JWT filter bean.
     * 
     * @return JWTFilter instance.
     */
    @Bean
    public OncePerRequestFilter authenticationJwtTokenFilter() {
        return new JWTFilter(jwtUtils, wis2UserDetailsService);
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