package com.mvo.individualsapi.service.impl;

import com.mvo.individualsapi.config.ApplicationConfiguration;
import com.mvo.individualsapi.dto.RegistrationResponseDTO;
import com.mvo.individualsapi.dto.RegistrationRequestDTO;
import com.mvo.individualsapi.service.RegistrationService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {
    private final WebClient webClient;
    private final Keycloak keycloakAdminClient;

    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Override
    public Mono<RegistrationResponseDTO> registrationUser(RegistrationRequestDTO request) {
        return createUserInKeycloak(request)
                .then(getToken(request.getEmail(), request.getPassword()))
                .doOnError(e -> log.error("Error during user registration: ", e));
    }

    private Mono<Void> createUserInKeycloak(RegistrationRequestDTO request) {
        return Mono.fromCallable(() -> {
                    UserRepresentation user = new UserRepresentation();
                    user.setEnabled(true);
                    user.setUsername("test");
                    user.setFirstName("test");
                    user.setLastName("test");
                    user.setEmail(request.getEmail());
                    user.setEmailVerified(false);

                    CredentialRepresentation credential = new CredentialRepresentation();
                    credential.setType(CredentialRepresentation.PASSWORD);
                    credential.setValue(request.getPassword());
                    credential.setTemporary(false);

                    user.setCredentials(Collections.singletonList(credential));

                    Response response = keycloakAdminClient.realm(realm).users().create(user);

                    if (response.getStatus() < 300) {
                        log.info("User created successfully in Keycloak");
                        return true;
                    } else {
                        String errorMessage = response.readEntity(String.class);
                        log.error("Failed to create user in Keycloak. Status: {}. Message: {}",
                                response.getStatus(), errorMessage);
                        throw new RuntimeException("Failed to create user: " + errorMessage);
                    }
                })
                .then();
    }

    private Mono<RegistrationResponseDTO> getToken(String username, String password) {
        return webClient.post()
                .uri(String.format("%s/realms/%s/protocol/openid-connect/token",
                        keycloakAuthServerUrl, realm))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("username", username)
                        .with("password", password))
                .retrieve()
                .bodyToMono(RegistrationResponseDTO.class)
                .doOnSuccess(s -> log.info("Token obtained successfully for user: {}", username))
                .doOnError(e -> log.error("Error obtaining token for user: {}", username, e));
    }
}