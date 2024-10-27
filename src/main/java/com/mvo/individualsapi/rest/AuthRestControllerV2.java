package com.mvo.individualsapi.rest;

import com.mvo.individualsapi.dto.AccessTokenDTO;
import com.mvo.individualsapi.service.keycloak_person_service.RegistrationUserService;
import com.mvo.individualsapi.service.keycloak_person_service.UserService;
import dto.AddressDTO;
import dto.CountryDTO;
import dto.RegistrationRequestDTO;
import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v2/auth/")
public class AuthRestControllerV2 {
    private final RegistrationUserService registrationUserService;
    private final UserService userService;


    @PostMapping("registration")
    public Mono<AccessTokenDTO> registrationUser(@RequestBody RegistrationRequestDTO request) {
        return registrationUserService.registrationUser(request);
    }

    @GetMapping("info")
    public Mono<UserDTO> userinfo(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(authentication -> authentication.getToken().getTokenValue())
                .flatMap(userService::getUserInfo);
    }

    @GetMapping("address")
    public Mono<AddressDTO> getUserAddress(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(authentication -> authentication.getToken().getTokenValue())
                .flatMap(userService::getUserAddress);
    }

    @GetMapping("country")
    public Mono<CountryDTO> getUserCountry(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(authentication -> authentication.getToken().getTokenValue())
                .flatMap(userService::getUserCountry);
    }

}
