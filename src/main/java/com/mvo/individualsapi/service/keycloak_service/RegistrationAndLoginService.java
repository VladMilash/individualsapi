package com.mvo.individualsapi.service.keycloak_service;

import com.mvo.individualsapi.dto.AccessTokenDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import reactor.core.publisher.Mono;

public interface RegistrationAndLoginService {
    Mono<AccessTokenDTO> registrationUser(RegistrationOrLoginRequestDTO request);
    Mono<AccessTokenDTO> loginUser(RegistrationOrLoginRequestDTO request);
}
