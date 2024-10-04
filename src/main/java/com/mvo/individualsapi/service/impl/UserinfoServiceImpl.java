package com.mvo.individualsapi.service.impl;

import com.mvo.individualsapi.dto.UserinfoResponseDTO;
import com.mvo.individualsapi.service.UserinfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserinfoServiceImpl implements UserinfoService {

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String keycloakIssuerUri;

    private final WebClient webClient;

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
}
