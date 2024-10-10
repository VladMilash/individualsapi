package com.mvo.individualsapi.service.impl;

import com.mvo.individualsapi.dto.RefreshTokenRequestDTO;
import com.mvo.individualsapi.dto.AccessTokenDto;
import com.mvo.individualsapi.service.RefreshTokenService;
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
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.keycloak.authorization-uri}")
    private String authorizationUri;

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
}
