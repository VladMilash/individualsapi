package com.mvo.individualsapi.service.person_service_client;

import dto.RegistrationRequestDTO;
import dto.UserDTO;
import reactor.core.publisher.Mono;

public interface PersonServiceClient {
    Mono<UserDTO> registrationUser(RegistrationRequestDTO request);
    Mono<Void> doRollBeckRegistration(RegistrationRequestDTO request);
}
