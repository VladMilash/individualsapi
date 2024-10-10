package com.mvo.individualsapi.service;

import com.mvo.individualsapi.dto.RefreshTokenRequestDTO;
import com.mvo.individualsapi.dto.AccessTokenDto;
import reactor.core.publisher.Mono;

public interface RefreshTokenService {
    Mono<AccessTokenDto> refreshToken(RefreshTokenRequestDTO request);
}
