package com.mvo.individualsapi.service;

import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginResponseDTO;
import reactor.core.publisher.Mono;

public interface LoginService {
    Mono<RegistrationOrLoginResponseDTO> login(RegistrationOrLoginRequestDTO registrationOrLoginRequestDTO);
}
