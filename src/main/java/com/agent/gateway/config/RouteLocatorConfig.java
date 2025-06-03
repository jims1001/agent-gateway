package com.agent.gateway.config;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
@Configuration
public class RouteLocatorConfig {


    // Global CORS Configuration Bean for resolving cross-origin issues.
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Should cookies be allowed?
        config.addAllowedOriginPattern("*"); // Allow all origins (recommended for Spring Boot 2.4+)
        config.addAllowedHeader("*"); // Allow all headers
        config.addAllowedMethod("*"); // Allow all request methods: GET, POST, PUT, DELETE, etc.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Effective for all routes.

        return new CorsWebFilter(source);
    }


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()

                .route("user_service_route", r -> r.path("/user/**")
                        .uri("http://localhost:8088"))
                .route("agent_server_web", r -> r.path("/agent/**") //
                        .filters(f -> f.stripPrefix(1)) //
                        .uri("http://localhost:8082"))

                // WebSocket request agent
                .route("ws_route", r -> r
                        .path("/socket.io/**")
                        .uri("ws://localhost:10000"))

                // HTTP requests are also proxied (Socket.IO's initial handshake uses HTTP).
                .route("http_route", r -> r
                        .path("/socket.io/**")
                        .uri("http://localhost:10000"))

                .build();
    }
}
