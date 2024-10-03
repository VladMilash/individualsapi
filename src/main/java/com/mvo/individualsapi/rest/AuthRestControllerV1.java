package com.mvo.individualsapi.rest;

import com.mvo.individualsapi.dto.RegistrationRequestDTO;
import com.mvo.individualsapi.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth/")
public class AuthRestControllerV1 {
    private final RegistrationService registrationService;

    @PostMapping("/registration")
    public Mono<ResponseEntity<String>> userRegistration(@RequestBody RegistrationRequestDTO registrationRequestDTO) {
        return registrationService.registrationUser(registrationRequestDTO)
                .map(responseDTO -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body("Registration successful"));
    }
}



