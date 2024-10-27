package com.mvo.individualsapi.service.keycloak_person_service;

import dto.AddressDTO;
import dto.CountryDTO;
import dto.UserDTO;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserDTO> getUserInfo(String token);

    Mono<AddressDTO> getUserAddress(String token);

    Mono<CountryDTO> getUserCountry(String token);
}
