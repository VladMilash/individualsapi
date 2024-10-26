package com.mvo.individualsapi.service.keycloak_person_service;

import dto.UserDTO;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserDTO> getUserInfo(String token);
}
