package com.mvo.individualsapi.service.keycloak_person_service.impl;

import com.mvo.individualsapi.dto.AccessTokenDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.exception.ApiException;
import com.mvo.individualsapi.service.keycloak_person_service.RegistrationUserService;
import com.mvo.individualsapi.service.keycloak_service.RegistrationAndLoginService;
import com.mvo.individualsapi.service.person_service_client.PersonServiceClient;
import dto.RegistrationRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegistrationUserServiceImpl implements RegistrationUserService {
    private final PersonServiceClient personServiceClient;
    private final RegistrationAndLoginService registrationAndLoginService;

    @Override
    public Mono<AccessTokenDTO> registrationUser(RegistrationRequestDTO request) {
        return personServiceClient.registrationUser(request)
                .switchIfEmpty(Mono.error(new ApiException("Registration user in person service error", "PERSON_SERVICE_REGISTRATION_ERROR")))
                .doOnError(error -> log.error("Registration user in person service error"))
                .then(registrationAndLoginService.registrationUser(new RegistrationOrLoginRequestDTO(request.email(),
                                request.password(), request.confirmPassword()))
                        .onErrorResume(error -> {
                            log.error("Registration user in keycloak error", error);
                            return personServiceClient.doRollBeckRegistration(request)
                                    .then(Mono.error(error));

                        })
                );
    }
}

