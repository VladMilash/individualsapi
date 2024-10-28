package com.mvo.individualsapi.it;

import com.mvo.individualsapi.dto.AccessTokenDTO;
import com.mvo.individualsapi.service.person_service_client.PersonServiceClient;
import dto.*;
import dto.status.Status;
import dto.status.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItAuthRestControllerV2Test {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PersonServiceClient personServiceClient;

    private RegistrationRequestDTO registrationRequestDTO;
    private UserDTO mockUserDTO;
    private String accessToken;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        webTestClient = webTestClient
                .mutate()
                .responseTimeout(Duration.ofSeconds(20))
                .build();

        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();

        mockUserDTO = new UserDTO(
                userId,
                "secretKey123",
                now,
                now,
                registrationRequestDTO.firstName(),
                registrationRequestDTO.lastName(),
                now,
                null,
                UserStatus.VERIFIED,
                true,
                addressId
        );

        when(personServiceClient.registrationUser(any(RegistrationRequestDTO.class)))
                .thenReturn(Mono.just(mockUserDTO));

        when(personServiceClient.getUserInfo(any(String.class)))
                .thenReturn(Mono.just(mockUserDTO));

        when(personServiceClient.getUserAddress(any(UUID.class)))
                .thenReturn(Mono.just(new AddressDTO(
                        UUID.randomUUID(),
                        now,
                        now,
                        1,
                        "123 Test St",
                        "12345",
                        null,
                        "Test City",
                        "Test State"
                )));

        when(personServiceClient.getUserCountry(any(UUID.class)))
                .thenReturn(Mono.just(new CountryDTO(
                        1,
                        now,
                        now,
                        "United States",
                        "US",
                        "USA",
                        Status.ACTIVE
                )));

        when(personServiceClient.getUserIndividual(any(UUID.class)))
                .thenReturn(Mono.just(new IndividualDTO(
                        UUID.randomUUID(),
                        userId,
                        now,
                        now,
                        "AB123456",
                        "+1234567890",
                        "test@example.com",
                        now,
                        null,
                        Status.ACTIVE
                )));

        when(personServiceClient.updateUser(any(UserDTO.class)))
                .thenReturn(Mono.just(new UserHistoryDTO(
                        UUID.randomUUID(),
                        now,
                        userId,
                        "USER",
                        "UPDATE",
                        "{\"status\": \"VERIFIED\"}"
                )));

        when(personServiceClient.updateUserAddress(any(UUID.class), any(AddressDTO.class)))
                .thenReturn(Mono.just(new UserHistoryDTO(
                        UUID.randomUUID(),
                        now,
                        userId,
                        "ADDRESS",
                        "UPDATE_ADDRESS",
                        "{\"address\": \"Updated\"}"
                )));

        when(personServiceClient.updateUserIndividuals(any(IndividualDTO.class)))
                .thenReturn(Mono.just(new UserHistoryDTO(
                        UUID.randomUUID(),
                        now,
                        userId,
                        "INDIVIDUAL",
                        "UPDATE_INDIVIDUAL",
                        "{\"status\": \"UPDATED\"}"
                )));
    }

    @Test
    void testUserRegistration_Success() {
        AccessTokenDTO registrationResponseDTO = webTestClient.post()
                .uri("/api/v2/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrationRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccessTokenDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(registrationResponseDTO);
        assertNotNull(registrationResponseDTO.getAccessToken());
        assertNotNull(registrationResponseDTO.getRefreshToken());

        accessToken = registrationResponseDTO.getAccessToken();
    }

    @Test
    void testUserInfo_Success() {
        testUserRegistration_Success();

        webTestClient.get()
                .uri("/api/v2/auth/info")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDTO.class)
                .value(userDTO -> {
                    assertNotNull(userDTO);
                    assertNotNull(userDTO.id());
                    assertNotNull(userDTO.firstName());
                    assertNotNull(userDTO.lastName());
                });
    }

    @Test
    void testGetUserAddress_Success() {
        testUserRegistration_Success();

        webTestClient.get()
                .uri("/api/v2/auth/address")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressDTO.class)
                .value(addressDTO -> {
                    assertNotNull(addressDTO);
                    assertNotNull(addressDTO.address());
                    assertNotNull(addressDTO.city());
                    assertNotNull(addressDTO.zipCode());
                });
    }

    @Test
    void testGetUserCountry_Success() {
        testUserRegistration_Success();

        webTestClient.get()
                .uri("/api/v2/auth/country")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CountryDTO.class)
                .value(countryDTO -> {
                    assertNotNull(countryDTO);
                    assertNotNull(countryDTO.alpha2());
                    assertNotNull(countryDTO.name());
                });
    }

    @Test
    void testGetUserIndividuals_Success() {
        testUserRegistration_Success();

        webTestClient.get()
                .uri("/api/v2/auth/individuals")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(IndividualDTO.class)
                .value(individualDTO -> {
                    assertNotNull(individualDTO);
                    assertNotNull(individualDTO.id());
                    assertNotNull(individualDTO.passportNumber());
                    assertNotNull(individualDTO.email());
                });
    }

    @Test
    void testUpdateUser_Success() {
        testUserRegistration_Success();

        UserDTO updateUserDTO = new UserDTO(
                mockUserDTO.id(),
                "newSecretKey",
                now,
                now,
                "UpdatedFirst",
                "UpdatedLast",
                now,
                null,
                UserStatus.VERIFIED,
                true,
                mockUserDTO.addressId()
        );

        webTestClient.put()
                .uri("/api/v2/auth")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateUserDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserHistoryDTO.class)
                .value(historyDTO -> {
                    assertNotNull(historyDTO);
                    assertNotNull(historyDTO.id());
                    assertNotNull(historyDTO.reason());
                });

    }

    @Test
    void testUpdateUserAddress_Success() {
        testUserRegistration_Success();

        AddressDTO addressDTO = new AddressDTO(
                UUID.randomUUID(),
                now,
                now,
                1,
                "456 New Street",
                "54321",
                null,
                "New City",
                "New State"
        );

        webTestClient.put()
                .uri("/api/v2/auth/address")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserHistoryDTO.class)
                .value(historyDTO -> {
                    assertNotNull(historyDTO);
                    assertNotNull(historyDTO.id());
                    assertNotNull(historyDTO.reason());
                });
    }

    @Test
    void testUpdateUserIndividuals_Success() {
        testUserRegistration_Success();
        IndividualDTO individualDTO = new IndividualDTO(
                UUID.randomUUID(),
                mockUserDTO.id(),
                now,
                now,
                "CD789012",
                "+9876543210",
                "updated@example.com",
                now,
                null,
                Status.ACTIVE
        );

        webTestClient.put()
                .uri("/api/v2/auth/individuals")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserHistoryDTO.class)
                .value(historyDTO -> {
                    assertNotNull(historyDTO);
                    assertNotNull(historyDTO.id());
                    assertNotNull(historyDTO.reason());
                });
    }
}