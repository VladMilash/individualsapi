package com.mvo.individualsapi.rest;

import com.mvo.individualsapi.dto.AccessTokenDTO;
import com.mvo.individualsapi.service.keycloak_person_service.RegistrationUserService;
import dto.RegistrationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v2/auth/")
public class AuthRestControllerV2 {
    private final RegistrationUserService registrationUserService;

    @PostMapping("registration")
    public Mono<AccessTokenDTO> registrationUser(@RequestBody RegistrationRequestDTO request) {
        return registrationUserService.registrationUser(request);
    }
}
