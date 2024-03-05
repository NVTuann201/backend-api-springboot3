package com.acme.backend.springboot.users.config;

import com.acme.backend.springboot.users.support.access.AccessController;
import com.acme.backend.springboot.users.support.keycloak.KeycloakJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration applied on all web endpoints defined for this
 * application. Any configuration on specific resources is applied
 * in addition to these global rules.
 */
@Configuration
@RequiredArgsConstructor
class WebSecurityConfig {

    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;

    /**
     * Configures basic security handler per HTTP session.
     * <p>
     * <ul>
     * <li>Stateless session (no session kept server-side)</li>
     * <li>CORS set up</li>
     * <li>Require the role "ACCESS" for all api paths</li>
     * <li>JWT converted into Spring token</li>
     * </ul>
     *
     * @param http security configuration
     * @throws Exception any error
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.sessionManagement(smc -> {
            smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });
        http.cors(this::configureCors);
        http.authorizeHttpRequests(ahrc -> {
            // declarative route configuration
            // .mvcMatchers("/api").hasAuthority("ROLE_ACCESS")
            ahrc.requestMatchers("/api/**").access(AccessController::checkAccess);
            // add additional routes
            ahrc.anyRequest().fullyAuthenticated(); //
        });
        http.oauth2ResourceServer(arsc -> {
            arsc.jwt(jc -> {
                jc.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter);
            });
        });

        return http.build();
    }

    @Bean
    AccessController accessController() {
        return new AccessController();
    }

    /**
     * Configures CORS to allow requests from localhost:30000
     *
     * @param cors mutable cors configuration
     */
    protected void configureCors(CorsConfigurer<HttpSecurity> cors) {

        UrlBasedCorsConfigurationSource defaultUrlBasedCorsConfigSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.addAllowedOrigin("*");
        List.of("GET", "POST", "PUT", "DELETE").forEach(corsConfiguration::addAllowedMethod);
        defaultUrlBasedCorsConfigSource.registerCorsConfiguration("/**", corsConfiguration);

        cors.configurationSource(req -> {

            CorsConfiguration config = new CorsConfiguration();

            config = config.combine(defaultUrlBasedCorsConfigSource.getCorsConfiguration(req));

            // check if request Header "origin" is in white-list -> dynamically generate cors config

            return config;
        });
    }
}