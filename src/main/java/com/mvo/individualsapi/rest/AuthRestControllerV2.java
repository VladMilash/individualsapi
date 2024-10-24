package com.mvo.individualsapi.rest;

import com.mvo.individualsapi.service.person_service_client.PersonServiceClient;
import dto.RegistrationRequestDTO;
import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v2/auth/")
public class AuthRestControllerV2 {
    private final PersonServiceClient personServiceClient;

    @PostMapping("registration")
    public Mono<UserDTO> registrationUser(@RequestBody RegistrationRequestDTO request) {
        return personServiceClient.registrationUser(request);
    }
}
