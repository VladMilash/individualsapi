package com.mvo.individualsapi.service.keycloak.impl;

import com.mvo.individualsapi.dto.AccessTokenDto;
import com.mvo.individualsapi.dto.RefreshTokenRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.dto.UserinfoResponseDTO;
import com.mvo.individualsapi.service.keycloak.KeyCloakClient;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class KeyCloakClientImpl implements KeyCloakClient {
    private final WebClient webClient;
    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String keycloakIssuerUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.keycloak.authorization-uri}")
    private String authorizationUri;

    @Value("${spring.security.oauth2.client.provider.keycloak.registration-uri}")
    private String registrationUri;

    @Override
    public Mono<String> getAdminToken() {
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

    @Override
    public Mono<Void> createUser(RegistrationOrLoginRequestDTO request, String adminToken) {
        Map<String, Object> userRepresentation = getRepresentation(request);
        return webClient.post()
                .uri(registrationUri)
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


    @Override
    public Mono<AccessTokenDto> getToken(String email, String password) {
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
                .bodyToMono(AccessTokenDto.class)
                .doOnSuccess(s -> log.info("Token obtained successfully"))
                .doOnError(e -> log.error("Error obtaining token", e));
    }

    @Override
    public Mono<AccessTokenDto> refreshToken(RefreshTokenRequestDTO request) {
        return webClient.post()
                .uri(authorizationUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "refresh_token")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("refresh_token", request.getRefreshToken())
                        .with("scope", "openid"))
                .retrieve()
                .bodyToMono(AccessTokenDto.class)
                .doOnSuccess(s -> log.info("Token refreshed successfully"))
                .doOnError(e -> log.error("Error refresh token", e));
    }

    @Override
    public Mono<UserinfoResponseDTO> getUserinfo(String token) {
        return webClient.get()
                .uri(keycloakIssuerUri + "/protocol/openid-connect/userinfo")
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(UserinfoResponseDTO.class)
                .doOnSuccess(response -> log.info("Success get userinfo"))
                .doOnError(e -> log.error("Error get userinfo: ", e));
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


}

