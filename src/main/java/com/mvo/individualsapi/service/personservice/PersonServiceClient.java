package com.mvo.individualsapi.service.personservice;

import com.mvo.individualsapi.dto.AccessTokenDTO;
import dto.RegistrationRequestDTO;
import dto.UserDTO;
import reactor.core.publisher.Mono;

public interface PersonServiceClient {
    Mono<UserDTO> registrationUser(RegistrationRequestDTO request);
}
