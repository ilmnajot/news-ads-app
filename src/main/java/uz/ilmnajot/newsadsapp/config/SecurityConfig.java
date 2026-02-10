package uz.ilmnajot.newsadsapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import uz.ilmnajot.newsadsapp.security.JwtFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtFilter jwtFilter;
        private final CustomAccessDeniedHandler customAccessDeniedHandler;
        private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

        @Bean
        // securityFilterChain
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(Customizer.withDefaults())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(WHITE_LIST).permitAll()
                                                .requestMatchers(BLACK_LIST).permitAll()
                                                .requestMatchers("/api/v1/admin/**").authenticated()
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                                .exceptionHandling(e -> e
                                                .accessDeniedHandler(customAccessDeniedHandler)
                                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                                .build();
        }

        private static final String[] WHITE_LIST = {

                        "/api/auth/**", // Your auth endpoints
                        "/v3/api-docs/**", // OpenAPI JSON
                        "/swagger-ui/**", // Swagger UI
                        "/swagger-ui.html", // Swagger UI HTML
                        "/swagger-resources/**", // Swagger resources
                        "/configuration/ui", // Swagger config
                        "/configuration/security", // Swagger security config
                        "/webjars/**",
                        "/actuator/**",
                        "/api/v1/public/**",
                        "/api/v1/admin/auth/login",
                        "/api/v1/admin/auth/refresh",
                        "/api/v1/admin/auth/register"
        };
        // to test for now
        private static final String[] BLACK_LIST = {
//                        "/api/v1/admin/categories/**", // rm
//                        "/api/v1/admin/media/**"
                        // "/api/v1/admin/auth/register"
        };

        @Bean
        // corsConfigurationSource
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of(
                                "http://localhost:3000",
                                "http://localhost:5173",
                                "https://domain.uz"));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
                configuration.setExposedHeaders(List.of("Authorization")); // Optional
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
