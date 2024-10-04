package com.mvo.individualsapi.service.impl;

import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginResponseDTO;
import com.mvo.individualsapi.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.keycloak.authorization-uri}")
    private String authorizationUri;

    @Override
    public Mono<RegistrationOrLoginResponseDTO> login(RegistrationOrLoginRequestDTO registrationOrLoginRequestDTO) {
        if (!registrationOrLoginRequestDTO.getPassword().equals(registrationOrLoginRequestDTO.getConfirmPassword())) {
            return Mono.error(new IllegalArgumentException("Passwords do not match"));
        }

        return webClient.post()
                .uri(authorizationUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("username", registrationOrLoginRequestDTO.getEmail())
                        .with("password",registrationOrLoginRequestDTO.getPassword())
                        .with("scope", "openid"))
                .retrieve()
                .bodyToMono(RegistrationOrLoginResponseDTO.class)
                .doOnSuccess(s -> log.info("Token obtained successfully"))
                .doOnError(e -> log.error("Error obtaining token", e));
    }

}
