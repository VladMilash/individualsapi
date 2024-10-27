package com.mvo.individualsapi.service.keycloak_person_service;

import dto.AddressDTO;
import dto.UserDTO;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserDTO> getUserInfo(String token);

    Mono<AddressDTO> getUserAddress(String token);
}
