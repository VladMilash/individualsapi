package com.mvo.individualsapi.it;

import com.mvo.individualsapi.dto.RefreshTokenRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginResponseDTO;
import com.mvo.individualsapi.dto.UserinfoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItAuthRestControllerV1Test {

    @Autowired
    private WebTestClient webTestClient;

    private RegistrationOrLoginRequestDTO requestDTO;
    private RegistrationOrLoginResponseDTO registrationResponseDTO;

    @BeforeEach
    void setUp() {
        webTestClient = webTestClient
                .mutate()
                .responseTimeout(Duration.ofSeconds(20))
                .build();

        requestDTO = RegistrationOrLoginRequestDTO.builder()
                .email("test" + System.currentTimeMillis() + "@example.com")
                .password("Password123!")
                .confirmPassword("Password123!")
                .build();
    }

    @Test
    void testUserRegistration_Success() {
        registrationResponseDTO = webTestClient.post()
                .uri("/api/v1/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RegistrationOrLoginResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(registrationResponseDTO);
        assertNotNull(registrationResponseDTO.getAccessToken());
        assertNotNull(registrationResponseDTO.getRefreshToken());
    }

    @Test
    void testUserLogin_Success() {
        testUserRegistration_Success();

        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RegistrationOrLoginResponseDTO.class)
                .value(loginResponse -> {
                    assertNotNull(loginResponse.getAccessToken());
                    assertNotNull(loginResponse.getRefreshToken());
                });
    }

    @Test
    void testUserInfo_Success() {
        testUserRegistration_Success();

        UserinfoResponseDTO userinfoResponseDTO = webTestClient.get()
                .uri("/api/v1/auth/info")
                .headers(headers -> headers.setBearerAuth(registrationResponseDTO.getAccessToken()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserinfoResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(userinfoResponseDTO);
        assertNotNull(userinfoResponseDTO.getSub());
        assertNotNull(userinfoResponseDTO.getPreferredUsername());
        assertNotNull(userinfoResponseDTO.getEmail());
    }

    @Test
    void testRefreshToken_Success() {
        testUserRegistration_Success();

        RefreshTokenRequestDTO refreshTokenRequestDTO = RefreshTokenRequestDTO.builder()
                .refreshToken(registrationResponseDTO.getRefreshToken())
                .build();

        RegistrationOrLoginResponseDTO responseDTO = webTestClient.post()
                .uri("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(refreshTokenRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RegistrationOrLoginResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getAccessToken());
        assertNotNull(responseDTO.getRefreshToken());
    }


}
