package com.mvo.individualsapi.service.keycloak_person_service.impl;

import com.mvo.individualsapi.exception.ApiException;
import com.mvo.individualsapi.service.keycloak_person_service.UserService;
import com.mvo.individualsapi.service.keycloak_service.UserinfoService;
import com.mvo.individualsapi.service.person_service_client.PersonServiceClient;
import dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserinfoService userinfoService;
    private final PersonServiceClient personServiceClient;

    @Override
    public Mono<UserDTO> getUserInfo(String token) {
        return userinfoService.getUserinfo(token)
                .flatMap(userinfoResponseDTO -> personServiceClient.getUserInfo(userinfoResponseDTO.getEmail()))
                .doOnSuccess(userDTO -> log.info("Operation for get user info, for user with id {} finished success", userDTO.id()))
                .doOnError(error -> log.error("Failed operation for get user info", error));
    }

    @Override
    public Mono<AddressDTO> getUserAddress(String token) {
        return userinfoService.getUserinfo(token)
                .flatMap(userinfoResponseDTO -> personServiceClient.getUserInfo(userinfoResponseDTO.getEmail()))
                .flatMap(userDTO -> personServiceClient.getUserAddress(userDTO.id()))
                .doOnSuccess(userDTO -> log.info("Operation for get user address, for user with id {} finished success", userDTO.id()))
                .doOnError(error -> log.error("Failed operation for get user address", error));

    }

    @Override
    public Mono<CountryDTO> getUserCountry(String token) {
        return userinfoService.getUserinfo(token)
                .flatMap(userinfoResponseDTO -> personServiceClient.getUserInfo(userinfoResponseDTO.getEmail()))
                .flatMap(userDTO -> personServiceClient.getUserCountry(userDTO.id()))
                .doOnSuccess(userDTO -> log.info("Operation for get user country, for user with id {} finished success", userDTO.id()))
                .doOnError(error -> log.error("Failed operation for get user country", error));
    }

    @Override
    public Mono<IndividualDTO> getUserIndividual(String token) {
        return userinfoService.getUserinfo(token)
                .flatMap(userinfoResponseDTO -> personServiceClient.getUserInfo(userinfoResponseDTO.getEmail()))
                .flatMap(userDTO -> personServiceClient.getUserIndividual(userDTO.id()))
                .doOnSuccess(userDTO -> log.info("Operation for get user individuals, for user with id {} finished success", userDTO.id()))
                .doOnError(error -> log.error("Failed operation for get user individuals", error));
    }

    @Override
    public Mono<UserHistoryDTO> updateUser(UserDTO userDTO) {
        return personServiceClient.updateUser(userDTO)
                .doOnSuccess(userHistoryDTO -> log.info("Operation for update user, for user with id {} finished success", userDTO.id()))
                .doOnError(error -> log.error("Failed operation for update user", error));
    }

    @Override
    public Mono<UserHistoryDTO> updateUserAddress(String token, AddressDTO addressDTO) {
        return userinfoService.getUserinfo(token)
                .flatMap(userinfoResponseDTO -> personServiceClient.getUserInfo(userinfoResponseDTO.getEmail()))
                .flatMap(userDTO -> personServiceClient.updateUserAddress(userDTO.id(), addressDTO))
                .doOnSuccess(userDTO -> log.info("Operation for update user address, for user with id {} finished success", userDTO.id()))
                .doOnError(error -> log.error("Failed operation for update user address", error));
    }

    @Override
    public Mono<UserHistoryDTO> updateUserIndividuals(IndividualDTO individualDTO) {
        return personServiceClient.updateUserIndividuals(individualDTO)
                .doOnSuccess(userHistoryDTO -> log.info("Operation for update user individuals, for user with id {} finished success", individualDTO.userId()))
                .doOnError(error -> log.error("Failed operation for update user individuals", error));
    }
}
