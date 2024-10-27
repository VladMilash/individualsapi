package com.mvo.individualsapi.rest;

import com.mvo.individualsapi.dto.AccessTokenDTO;
import com.mvo.individualsapi.service.keycloak_person_service.RegistrationUserService;
import com.mvo.individualsapi.service.keycloak_person_service.UserService;
import dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v2/auth")
public class AuthRestControllerV2 {
    private final RegistrationUserService registrationUserService;
    private final UserService userService;


    @PostMapping("/registration")
    public Mono<AccessTokenDTO> registrationUser(@RequestBody RegistrationRequestDTO request) {
        return registrationUserService.registrationUser(request);
    }

    @GetMapping("/info")
    public Mono<UserDTO> userinfo(ServerWebExchange exchange) {
        return getMap(exchange)
                .flatMap(userService::getUserInfo);
    }

    @GetMapping("/address")
    public Mono<AddressDTO> getUserAddress(ServerWebExchange exchange) {
        return getMap(exchange)
                .flatMap(userService::getUserAddress);
    }

    @GetMapping("/country")
    public Mono<CountryDTO> getUserCountry(ServerWebExchange exchange) {
        return getMap(exchange)
                .flatMap(userService::getUserCountry);
    }

    @GetMapping("/individuals")
    public Mono<IndividualDTO> getUserIndividuals(ServerWebExchange exchange) {
        return getMap(exchange)
                .flatMap(userService::getUserIndividual);
    }

    @PutMapping
    public Mono<UserHistoryDTO> updateUser(@RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    @PutMapping("/address")
    public Mono<UserHistoryDTO> updateUserAddress(ServerWebExchange exchange, @RequestBody AddressDTO addressDTO) {
        return getMap(exchange)
                .flatMap(token -> userService.updateUserAddress(token, addressDTO));
    }

    @PutMapping("/individuals")
    public Mono<UserHistoryDTO> updateUserIndividuals(@RequestBody IndividualDTO individualDTO) {
        return userService.updateUserIndividuals(individualDTO);
    }


    private Mono<String> getMap(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(authentication -> authentication.getToken().getTokenValue());
    }

}
