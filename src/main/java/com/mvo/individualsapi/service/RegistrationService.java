package com.mvo.individualsapi.service;

import com.mvo.individualsapi.dto.RegistrationOrLoginResponseDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import reactor.core.publisher.Mono;

public interface RegistrationService {
    Mono<RegistrationOrLoginResponseDTO> registrationUser(RegistrationOrLoginRequestDTO response);
}
