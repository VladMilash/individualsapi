package com.mvo.individualsapi.service.keycloak_client;

import com.mvo.individualsapi.dto.AccessTokenDTO;
import com.mvo.individualsapi.dto.RefreshTokenRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.dto.UserinfoResponseDTO;
import reactor.core.publisher.Mono;

public interface KeyCloakClient {
    Mono<String> getAdminToken();

    Mono<Void> createUser(RegistrationOrLoginRequestDTO request, String adminToken);

    Mono<AccessTokenDTO> getToken(String email, String password);

    Mono<AccessTokenDTO> refreshToken(RefreshTokenRequestDTO request);

    Mono<UserinfoResponseDTO> getUserinfo(String token);
}
