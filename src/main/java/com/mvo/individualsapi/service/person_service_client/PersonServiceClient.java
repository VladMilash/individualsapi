package com.mvo.individualsapi.service.person_service_client;

import dto.AddressDTO;
import dto.RegistrationRequestDTO;
import dto.UserDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PersonServiceClient {
    Mono<UserDTO> registrationUser(RegistrationRequestDTO request);

    Mono<Void> doRollBeckRegistration(UUID userId);

    Mono<UserDTO> getUserInfo(String email);

    Mono<AddressDTO> getUserAddress(UUID userId);
}
