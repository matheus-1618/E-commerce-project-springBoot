package com.example.cartservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration for all cart-service REST endpoints.
 *
 * <p>Registers a permissive CORS policy for the Phase 2.1 development environment
 * so that the web frontend ({@code microservices/web-frontend/index.html}) served from
 * a different port or origin can call the cart-service API without browser CORS blocks.
 *
 * <p><strong>Phase 2.1 / Development configuration:</strong>
 * {@code allowedOrigins("*")} is intentionally broad for local development convenience.
 * This must be narrowed to the specific frontend origin before any staging or production
 * deployment (pre-production CORS hardening documented in the security-design artifact).
 *
 * <p>CORS settings applied to {@code /api/**}:
 * <ul>
 *   <li>{@code allowedOrigins}: {@code *} (all origins — dev only)</li>
 *   <li>{@code allowedMethods}: GET, POST, PUT, DELETE, OPTIONS</li>
 *   <li>{@code allowedHeaders}: {@code *} (all headers)</li>
 *   <li>{@code allowCredentials}: {@code false} (wildcard origin prohibits credentials)</li>
 *   <li>{@code maxAge}: 3600 seconds (preflight cache duration)</li>
 * </ul>
 *
 * <p>Spring's CORS infrastructure handles the actual preflight response. No controller
 * code is needed for OPTIONS requests.
 *
 * <p>Stack: Spring Boot 2.6.4 / Java 11
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Registers the CORS mapping for all {@code /api/**} paths.
     *
     * <p>Spring calls this method during application startup to populate the MVC
     * {@code CorsConfigurationSource}. No constructor injection is needed — Spring
     * builds this configuration bean with no dependencies.
     *
     * @param registry the CORS registry provided by the Spring MVC framework
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
