package com.mvo.individualsapi.service.keycloak_service.impl;

import com.mvo.individualsapi.dto.AccessTokenDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.exception.PasswordsMatchException;
import com.mvo.individualsapi.service.keycloak_service.RegistrationAndLoginService;
import com.mvo.individualsapi.service.keycloak_client.KeyCloakClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegistrationAndLoginServiceImpl implements RegistrationAndLoginService {

    private final KeyCloakClient keyCloakClient;

    @Override
    public Mono<AccessTokenDTO> registrationUser(RegistrationOrLoginRequestDTO request) {
        return validatePasswords(request)
                .then(keyCloakClient.getAdminToken())
                .flatMap(adminToken -> keyCloakClient.createUser(request, adminToken))
                .then(keyCloakClient.getToken(request.getEmail(), request.getPassword()))
                .doOnError(e -> log.error("Error during user registration: ", e));
    }

    @Override
    public Mono<AccessTokenDTO> loginUser(RegistrationOrLoginRequestDTO request) {
        return validatePasswords(request)
                .then(keyCloakClient.getToken(request.getEmail(), request.getPassword()))
                .doOnSuccess(response -> log.info("Success login user with email {} ", request.getEmail()))
                .doOnError(e -> log.error("Error during user login: ", e));
    }

    private Mono<Void> validatePasswords(RegistrationOrLoginRequestDTO request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return Mono.error(new PasswordsMatchException("Passwords do not match", "PASSWORDS_DO_NOT_MATCH"));
        }
        return Mono.empty();
    }
}
