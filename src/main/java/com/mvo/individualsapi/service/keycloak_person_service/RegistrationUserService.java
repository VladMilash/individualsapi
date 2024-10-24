package com.mvo.individualsapi.service.keycloak_person_service;

import com.mvo.individualsapi.dto.AccessTokenDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import dto.RegistrationRequestDTO;
import reactor.core.publisher.Mono;

public interface RegistrationUserService {
    Mono<AccessTokenDTO> registrationUser(RegistrationRequestDTO request);
}
