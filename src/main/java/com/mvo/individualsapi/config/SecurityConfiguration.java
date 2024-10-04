package com.mvo.individualsapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.stream.Stream;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder
                .withJwkSetUri("http://localhost:8180/realms/individualsAPI/protocol/openid-connect/certs")
                .build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(customizer -> customizer
                        .pathMatchers("api/v1/auth/registration","api/v1/auth/login").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(customizer -> customizer.jwt(jwt -> {
                    ReactiveJwtAuthenticationConverter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
                    jwtAuthenticationConverter.setPrincipalClaimName("email");

                    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

                    JwtGrantedAuthoritiesConverter customJwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                    customJwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("groups");
                    customJwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

                    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                            new ReactiveJwtGrantedAuthoritiesConverterAdapter(token ->
                                    Stream.concat(jwtGrantedAuthoritiesConverter.convert(token).stream(),
                                                    customJwtGrantedAuthoritiesConverter.convert(token).stream())
                                            .toList()));
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter);
                }))
                .build();
    }
}
