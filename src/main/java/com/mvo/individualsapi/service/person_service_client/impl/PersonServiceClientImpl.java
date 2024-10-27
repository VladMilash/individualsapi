package com.mvo.individualsapi.service.person_service_client.impl;

import com.mvo.individualsapi.service.person_service_client.PersonServiceClient;
import dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${person-service.base-url}")
    private String personServiceBaseUrl;

    @Value("${person-service.registration-url}")
    private String personServiceRegistrationUrl;

    @Override
    public Mono<UserDTO> registrationUser(RegistrationRequestDTO request) {
        log.info("Sending request: {}", request);
        return webClient.post()
                .uri(personServiceRegistrationUrl)
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
                .uri(personServiceRegistrationUrl + "rollback/{userId}", userId)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(response -> log.info("RollBeck operation finished success"))
                .doOnError(error -> log.error("Failed to rollback registration", error))
                .then();
    }

    @Override
    public Mono<UserDTO> getUserInfo(String email) {
        return webClient.get()
                .uri(personServiceBaseUrl + "email/{email}", email)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .doOnNext(response -> log.info("Response received: {}", response))
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()));
    }

    @Override
    public Mono<AddressDTO> getUserAddress(UUID userId) {
        return webClient.get()
                .uri(personServiceBaseUrl + "address/{userId}", userId)
                .retrieve()
                .bodyToMono(AddressDTO.class)
                .doOnNext(response -> log.info("Response received: {}", response))
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()));
    }

    @Override
    public Mono<CountryDTO> getUserCountry(UUID userId) {
        return webClient.get()
                .uri(personServiceBaseUrl + "country/{userId}", userId)
                .retrieve()
                .bodyToMono(CountryDTO.class)
                .doOnNext(response -> log.info("Response received: {}", response))
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()));
    }

    @Override
    public Mono<IndividualDTO> getUserIndividual(UUID userId) {
        return webClient.get()
                .uri(personServiceBaseUrl + "individuals/{userId}", userId)
                .retrieve()
                .bodyToMono(IndividualDTO.class)
                .doOnNext(response -> log.info("Response received: {}", response))
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()));
    }

    @Override
    public Mono<UserHistoryDTO> updateUser(UserDTO userDTO) {
        return webClient.put()
                .uri(personServiceBaseUrl)
                .bodyValue(userDTO)
                .retrieve()
                .bodyToMono(UserHistoryDTO.class)
                .doOnNext(response -> log.info("Response received: {}", response))
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()));
    }

    @Override
    public Mono<UserHistoryDTO> updateUserAddress(UUID userId, AddressDTO addressDTO) {
        return webClient.put()
                .uri(personServiceBaseUrl + "address/{userId}", userId)
                .bodyValue(addressDTO)
                .retrieve()
                .bodyToMono(UserHistoryDTO.class)
                .doOnNext(response -> log.info("Response received: {}", response))
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()));
    }

    @Override
    public Mono<UserHistoryDTO> updateUserIndividuals(IndividualDTO individualDTO) {
        return webClient.put()
                .uri(personServiceBaseUrl + "individuals")
                .bodyValue(individualDTO)
                .retrieve()
                .bodyToMono(UserHistoryDTO.class)
                .doOnNext(response -> log.info("Response received: {}", response))
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()));
    }
}
