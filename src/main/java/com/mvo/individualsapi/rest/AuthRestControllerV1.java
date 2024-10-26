package com.mvo.individualsapi.rest;

import com.mvo.individualsapi.dto.RefreshTokenRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.dto.AccessTokenDTO;
import com.mvo.individualsapi.dto.UserinfoResponseDTO;
import com.mvo.individualsapi.service.keycloak_service.RefreshTokenService;
import com.mvo.individualsapi.service.keycloak_service.RegistrationAndLoginService;
import com.mvo.individualsapi.service.keycloak_service.UserinfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth/")
public class AuthRestControllerV1 {
    private final RegistrationAndLoginService registrationAndLoginService;
    private final UserinfoService userinfoService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/registration")
    public Mono<ResponseEntity<AccessTokenDTO>> userRegistration
            (@RequestBody RegistrationOrLoginRequestDTO registrationOrLoginRequestDTO) {

        return registrationAndLoginService.registrationUser(registrationOrLoginRequestDTO)
                .map(responseDTO -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(responseDTO));
    }

    @GetMapping("/info")
    public Mono<ResponseEntity<UserinfoResponseDTO>> userinfo(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(authentication -> authentication.getToken().getTokenValue())
                .flatMap(token -> userinfoService.getUserinfo(token)
                        .map(userinfoResponseDTO -> ResponseEntity
                                .status(HttpStatus.OK)
                                .body(userinfoResponseDTO)));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AccessTokenDTO>> login
            (@RequestBody RegistrationOrLoginRequestDTO registrationOrLoginRequestDTO) {

        return registrationAndLoginService.loginUser(registrationOrLoginRequestDTO)
                .map(responseDTO -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(responseDTO));

    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<AccessTokenDTO>> refresh
            (@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {

        return refreshTokenService.refreshToken(refreshTokenRequestDTO)
                .map(responseDTO -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(responseDTO));

    }
}



