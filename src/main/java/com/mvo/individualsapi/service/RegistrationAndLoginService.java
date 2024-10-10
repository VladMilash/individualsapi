package com.mvo.individualsapi.service;

import com.mvo.individualsapi.dto.AccessTokenDto;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import reactor.core.publisher.Mono;

public interface RegistrationAndLoginService {
    Mono<AccessTokenDto> registrationUser(RegistrationOrLoginRequestDTO request);
    Mono<AccessTokenDto> loginUser(RegistrationOrLoginRequestDTO request);
}
