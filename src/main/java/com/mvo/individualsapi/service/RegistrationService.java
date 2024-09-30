package com.mvo.individualsapi.service;

import com.mvo.individualsapi.dto.RegistrationResponseDTO;
import com.mvo.individualsapi.dto.RegistrationRequestDTO;
import reactor.core.publisher.Mono;

public interface RegistrationService {
    Mono<RegistrationResponseDTO> registrationUser(RegistrationRequestDTO response);
}
