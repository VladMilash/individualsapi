package com.mvo.individualsapi.rest;

import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginResponseDTO;
import com.mvo.individualsapi.dto.UserinfoResponseDTO;
import com.mvo.individualsapi.service.RegistrationAndLoginService;
import com.mvo.individualsapi.service.UserinfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth/")
public class AuthRestControllerV1 {
    private final RegistrationAndLoginService registrationAndLoginService;
    private final UserinfoService userinfoService;

    @PostMapping("/registration")
    public Mono<ResponseEntity<RegistrationOrLoginResponseDTO>> userRegistration(@RequestBody RegistrationOrLoginRequestDTO registrationOrLoginRequestDTO) {
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
    public Mono<ResponseEntity<RegistrationOrLoginResponseDTO>> login(@RequestBody RegistrationOrLoginRequestDTO registrationOrLoginRequestDTO) {
        return registrationAndLoginService.loginUser(registrationOrLoginRequestDTO)
                .map(responseDTO -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(responseDTO));

    }
}



