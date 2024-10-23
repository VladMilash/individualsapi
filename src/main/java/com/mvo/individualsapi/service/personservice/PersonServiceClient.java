package com.mvo.individualsapi.service.personservice;

import com.mvo.individualsapi.dto.AccessTokenDTO;
import dto.RegistrationRequestDTO;
import dto.UserDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PersonServiceClient {
    Mono<UserDTO> registrationUser(RegistrationRequestDTO request);
    Flux<UserDTO> getAllUser();
}
