package com.mvo.individualsapi.service.keycloak_person_service;

import com.mvo.individualsapi.dto.AccessTokenDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import dto.RegistrationRequestDTO;
import dto.UserDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RegistrationUserService {
    Mono<AccessTokenDTO> registrationUser(RegistrationRequestDTO request);
}
