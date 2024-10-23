package com.mvo.individualsapi.service.personservice.impl;

import com.mvo.individualsapi.dto.AccessTokenDTO;
import com.mvo.individualsapi.service.personservice.PersonServiceClient;
import dto.RegistrationRequestDTO;
import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.james.mime4j.dom.Body;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonServiceClientImpl implements PersonServiceClient {
    private final WebClient webClient;

    @Override
    public Mono<UserDTO> registrationUser(RegistrationRequestDTO request) {
        return webClient.post()
                .uri("http://localhost:8084/v1/api/registration/")
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(UserDTO.class);
    }
}
