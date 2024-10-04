package com.mvo.individualsapi.service.impl;

import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginResponseDTO;
import com.mvo.individualsapi.service.RefreshTokenService;
import reactor.core.publisher.Mono;

public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Override
    public Mono<RegistrationOrLoginResponseDTO> refreshToken(RegistrationOrLoginRequestDTO request) {
        return null;
    }
}
