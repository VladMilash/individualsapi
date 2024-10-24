package com.mvo.individualsapi.service.keycloak_service;

import com.mvo.individualsapi.dto.RefreshTokenRequestDTO;
import com.mvo.individualsapi.dto.AccessTokenDTO;
import reactor.core.publisher.Mono;

public interface RefreshTokenService {
    Mono<AccessTokenDTO> refreshToken(RefreshTokenRequestDTO request);
}
