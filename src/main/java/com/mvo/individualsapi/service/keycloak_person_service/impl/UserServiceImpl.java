package com.mvo.individualsapi.service.keycloak_person_service.impl;

import com.mvo.individualsapi.exception.ApiException;
import com.mvo.individualsapi.service.keycloak_person_service.UserService;
import com.mvo.individualsapi.service.keycloak_service.UserinfoService;
import com.mvo.individualsapi.service.person_service_client.PersonServiceClient;
import dto.AddressDTO;
import dto.CountryDTO;
import dto.IndividualDTO;
import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
                .doOnSuccess(userDTO -> log.info("Operation get user info, for user with id {} finished success", userDTO.id()))
                .doOnError(error -> log.error("Failed operation get user info", error));
    }

    @Override
    public Mono<AddressDTO> getUserAddress(String token) {
        return userinfoService.getUserinfo(token)
                .flatMap(userinfoResponseDTO -> personServiceClient.getUserInfo(userinfoResponseDTO.getEmail()))
                .flatMap(userDTO -> personServiceClient.getUserAddress(userDTO.id()))
                .doOnSuccess(userDTO -> log.info("Operation get user address, for user with id {} finished success", userDTO.id()))
                .doOnError(error -> log.error("Failed operation get user address", error));

    }

    @Override
    public Mono<CountryDTO> getUserCountry(String token) {
        return userinfoService.getUserinfo(token)
                .flatMap(userinfoResponseDTO -> personServiceClient.getUserInfo(userinfoResponseDTO.getEmail()))
                .flatMap(userDTO -> personServiceClient.getUserCountry(userDTO.id()))
                .doOnSuccess(userDTO -> log.info("Operation get user country, for user with id {} finished success", userDTO.id()))
                .doOnError(error -> log.error("Failed operation get user country", error));
    }

    @Override
    public Mono<IndividualDTO> getUserIndividual(String token) {
        return userinfoService.getUserinfo(token)
                .flatMap(userinfoResponseDTO -> personServiceClient.getUserInfo(userinfoResponseDTO.getEmail()))
                .flatMap(userDTO -> personServiceClient.getUserIndividual(userDTO.id()))
                .doOnSuccess(userDTO -> log.info("Operation get user individuals, for user with id {} finished success", userDTO.id()))
                .doOnError(error -> log.error("Failed operation get user individuals", error));
    }
}
