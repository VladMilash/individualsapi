package com.mvo.individualsapi.service.keycloak;

import com.mvo.individualsapi.dto.AccessTokenDto;
import com.mvo.individualsapi.dto.RefreshTokenRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.dto.UserinfoResponseDTO;
import reactor.core.publisher.Mono;

public interface KeyCloakClient {
    Mono<String> getAdminToken();

    Mono<Void> createUser(RegistrationOrLoginRequestDTO request, String adminToken);

    Mono<AccessTokenDto> getToken(String email, String password);

    Mono<AccessTokenDto> refreshToken(RefreshTokenRequestDTO request);

    Mono<UserinfoResponseDTO> getUserinfo(String token);
}
