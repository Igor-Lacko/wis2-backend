package IIS.wis2_backend.Config.Auth;

import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
class JWTEntryPoint implements AuthenticationEntryPoint {
    /**
     * Handles unauthorized access attempts by sending a 401 Unauthorized response.
     * 
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param authException The authentication exception.
     */
    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}