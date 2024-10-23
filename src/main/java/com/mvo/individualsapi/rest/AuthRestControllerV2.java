package com.mvo.individualsapi.rest;

import com.mvo.individualsapi.service.personservice.PersonServiceClient;
import dto.RegistrationRequestDTO;
import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v2/auth/")
public class AuthRestControllerV2 {
    private final PersonServiceClient personServiceClient;

    @PostMapping("/registration")
    public Mono<UserDTO> registrationUser(RegistrationRequestDTO request) {
        return personServiceClient.registrationUser(request);
    }
}
