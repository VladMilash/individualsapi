package com.mvo.individualsapi.service.impl;

import com.mvo.individualsapi.dto.RegistrationResponseDTO;
import com.mvo.individualsapi.dto.RegistrationRequestDTO;
import com.mvo.individualsapi.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {
    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String keycloakIssuerUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Override
    public Mono<RegistrationResponseDTO> registrationUser(RegistrationRequestDTO request) {
        return getAdminToken()
                .flatMap(adminToken -> createUser(request, adminToken))
                .then(getToken(request.getEmail(), request.getPassword()))
                .doOnError(e -> log.error("Error during user registration: ", e));
    }

    private Mono<String> getAdminToken() {
        return webClient.post()
                .uri("http://localhost:8180/realms/individualsAPI/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> "Bearer " + response.get("access_token"))
                .doOnSuccess(response -> log.info("Success get admin token for client_id {},", clientId ))
                .doOnError(e -> log.error("Error obtaining admin token: ", e));
    }

    private Mono<Void> createUser(RegistrationRequestDTO request, String adminToken) {
        Map<String, Object> userRepresentation = getRepresentation(request);
        return webClient.post()
                .uri("http://localhost:8180/admin/realms/individualsAPI/users")
                .headers(headers -> {
                    headers.setBearerAuth(adminToken.substring(7));
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(BodyInserters.fromValue(userRepresentation))
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.CREATED) {
                        log.info("User created successfully");
                        return Mono.empty();
                    } else {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Failed to create user. Status: {}. Body: {}",
                                            response.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("Failed to create user: " + errorBody));
                                });
                    }
                });
    }


    private static Map<String, Object> getRepresentation(RegistrationRequestDTO request) {
        return Map.of(
                "username", request.getEmail(),
                "email", request.getEmail(),
                "enabled", true,
                "emailVerified", true,
                "credentials", java.util.List.of(
                        Map.of(
                                "type", "password",
                                "value", request.getPassword(),
                                "temporary", false
                        )
                )
        );
    }

    private Mono<RegistrationResponseDTO> getToken(String email, String password) {
        return webClient.post()
                .uri("http://localhost:8180/realms/individualsAPI/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("username", email)
                        .with("password", password))
                .retrieve()
                .bodyToMono(RegistrationResponseDTO.class)
                .doOnSuccess(s -> log.info("Token obtained successfully"))
                .doOnError(e -> log.error("Error obtaining token", e));
    }
}
