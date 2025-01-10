package v1.erpback.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import v1.erpback.auth.util.JwtAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages={"v1.erpback.auth.util"})
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/user/login").permitAll()
                        .requestMatchers("/api/v1/user/findUser","/api/v1/user/email","/api/v1/user/refresh","/api/v1/company/logo","/upload/**","/api/v1/user/create", "/api/v1/user/sendEmail", "/api/v1/user/verifyEmail", "/api/v1/user/password-reset","/api/v1/user/findAccount","/api/v1/position/list" ,"/api/v1/company/list", "/api/v1/department/list").permitAll()
                        .requestMatchers("/ws/**").permitAll()
//                        .requestMatchers("/ws/**").authenticated()
                        .anyRequest().authenticated())
                .exceptionHandling(configurer -> {
                    configurer.authenticationEntryPoint(((request, response, authException) -> {
                        response.setContentType("application/json");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"error\": \"Unauthorized access. Please login.\"}");
                    }));
                    configurer.accessDeniedHandler((((request, response, accessDeniedException) -> {
                        response.sendRedirect("/access-denied");
                    })));
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://192.168.0.121:3000","http://192.168.0.101:3000","http://localhost:8080", "http://localhost:3000","http://192.168.50.5:3000"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","OPTIONS"));
        corsConfiguration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Access-Token",
                "Refresh-Token",
                "Access-Token-Exp",
                "Refresh-Token-Exp"
        ));
        corsConfiguration.setExposedHeaders(Arrays.asList(
                "Access-Token", "Refresh-Token", "Access-Token-Exp", "Refresh-Token-Exp"
        ));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }



}
