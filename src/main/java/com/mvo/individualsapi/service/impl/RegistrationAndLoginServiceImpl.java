package com.mvo.individualsapi.service.impl;

import com.mvo.individualsapi.dto.RegistrationOrLoginResponseDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.service.RegistrationAndLoginService;
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
public class RegistrationAndLoginServiceImpl implements RegistrationAndLoginService {
    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String keycloakIssuerUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.keycloak.authorization-uri}")
    private String authorizationUri;

    @Override
    public Mono<RegistrationOrLoginResponseDTO> registrationUser(RegistrationOrLoginRequestDTO request) {
        return validatePasswords(request)
                .then(getAdminToken())
                .flatMap(adminToken -> createUser(request, adminToken))
                .then(getToken(request.getEmail(), request.getPassword()))
                .doOnError(e -> log.error("Error during user registration: ", e));
    }

    @Override
    public Mono<RegistrationOrLoginResponseDTO> loginUser(RegistrationOrLoginRequestDTO request) {
        return validatePasswords(request)
                .then(getToken(request.getEmail(), request.getPassword()))
                .doOnSuccess(response -> log.info("Success login user with email {} ", request.getEmail()))
                .doOnError(e -> log.error("Error during user login: ", e));
    }

    private Mono<Void> validatePasswords(RegistrationOrLoginRequestDTO request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return Mono.error(new IllegalArgumentException("Passwords do not match"));
        }
        return Mono.empty();
    }

    private Mono<String> getAdminToken() {
        return webClient.post()
                .uri(keycloakIssuerUri + "/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> "Bearer " + response.get("access_token"))
                .doOnSuccess(response -> log.info("Success get admin token for client_id {},", clientId))
                .doOnError(e -> log.error("Error obtaining admin token: ", e));
    }

    private Mono<Void> createUser(RegistrationOrLoginRequestDTO request, String adminToken) {
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


    private static Map<String, Object> getRepresentation(RegistrationOrLoginRequestDTO request) {
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

    private Mono<RegistrationOrLoginResponseDTO> getToken(String email, String password) {
        return webClient.post()
                .uri(authorizationUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("username", email)
                        .with("password", password)
                        .with("scope", "openid"))
                .retrieve()
                .bodyToMono(RegistrationOrLoginResponseDTO.class)
                .doOnSuccess(s -> log.info("Token obtained successfully"))
                .doOnError(e -> log.error("Error obtaining token", e));
    }
}