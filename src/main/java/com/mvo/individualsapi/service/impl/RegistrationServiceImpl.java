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
        return createUser(request)
                .then(getToken(request.getEmail(), request.getPassword()));
    }

    private Mono<Void> createUser(RegistrationRequestDTO request) {
        Map<String, Object> userRepresentation = getRepresentation(request);
        return webClient.post()
                .uri(keycloakIssuerUri + "/admin/realms/individualsAPI/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userRepresentation))
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.CREATED) {
                        log.info("User created successfully");
                        return Mono.empty();
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                })
                .then();
    }

    private static Map<String, Object> getRepresentation(RegistrationRequestDTO request) {
        return Map.of(
                "email", request.getEmail(),
                "enabled", true,
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
                .uri(keycloakIssuerUri + "/protocol/openid-connect/token")
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
