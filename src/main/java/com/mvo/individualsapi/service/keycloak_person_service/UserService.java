package com.mvo.individualsapi.service.keycloak_person_service;

import dto.*;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserDTO> getUserInfo(String token);

    Mono<AddressDTO> getUserAddress(String token);

    Mono<CountryDTO> getUserCountry(String token);

    Mono<IndividualDTO> getUserIndividual(String token);

    Mono<UserHistoryDTO> updateUser(UserDTO userDTO);
}
