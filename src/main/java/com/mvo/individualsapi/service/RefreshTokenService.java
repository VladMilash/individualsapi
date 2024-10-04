package com.mvo.individualsapi.service;

import com.mvo.individualsapi.dto.RefreshTokenRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginResponseDTO;
import reactor.core.publisher.Mono;

public interface RefreshTokenService {
    Mono<RegistrationOrLoginResponseDTO> refreshToken(RefreshTokenRequestDTO request);
}
