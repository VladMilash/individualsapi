package com.mvo.individualsapi.service.impl;

import com.mvo.individualsapi.dto.RefreshTokenRequestDTO;
import com.mvo.individualsapi.dto.AccessTokenDto;
import com.mvo.individualsapi.service.RefreshTokenService;
import com.mvo.individualsapi.service.keycloak.KeyCloakClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final KeyCloakClient keyCloakClient;
    
    @Override
    public Mono<AccessTokenDto>  refreshToken(RefreshTokenRequestDTO request) {
        return keyCloakClient.refreshToken(request);
    }
}
