package com.mvo.individualsapi.service.person_service_client.impl;

import com.mvo.individualsapi.service.person_service_client.PersonServiceClient;
import dto.AddressDTO;
import dto.RegistrationRequestDTO;
import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonServiceClientImpl implements PersonServiceClient {
    private final WebClient webClient;

    @Override
    public Mono<UserDTO> registrationUser(RegistrationRequestDTO request) {
        log.info("Sending request: {}", request);
        return webClient.post()
                .uri("http://localhost:8084/v1/api/registration/")
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .doOnNext(response -> log.info("Response received: {}", response))
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()));
    }

    @Override
    public Mono<Void> doRollBeckRegistration(UUID userId) {
        return webClient.delete()
                .uri("http://localhost:8084/v1/api/registration/rollback/{userId}", userId)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(response -> log.info("RollBeck operation finished success"))
                .doOnError(error -> log.error("Failed to rollback registration", error))
                .then();
    }

    @Override
    public Mono<UserDTO> getUserInfo(String email) {
        return webClient.get()
                .uri("http://localhost:8084/v1/api/users/email/{email}", email)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .doOnNext(response -> log.info("Response received: {}", response))
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()));
    }

    @Override
    public Mono<AddressDTO> getUserAddress(UUID userId) {
        return webClient.get()
                .uri("http://localhost:8084/v1/api/users/address/{userId}", userId)
                .retrieve()
                .bodyToMono(AddressDTO.class)
                .doOnNext(response -> log.info("Response received: {}", response))
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()));
    }
}
