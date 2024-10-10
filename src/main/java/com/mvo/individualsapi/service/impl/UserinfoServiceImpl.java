package com.mvo.individualsapi.service.impl;

import com.mvo.individualsapi.dto.UserinfoResponseDTO;
import com.mvo.individualsapi.service.UserinfoService;
import com.mvo.individualsapi.service.keycloak.KeyCloakClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserinfoServiceImpl implements UserinfoService {
    private final KeyCloakClient keyCloakClient;

    @Override
    public Mono<UserinfoResponseDTO> getUserinfo(String token) {
        return keyCloakClient.getUserinfo(token);
    }
}
