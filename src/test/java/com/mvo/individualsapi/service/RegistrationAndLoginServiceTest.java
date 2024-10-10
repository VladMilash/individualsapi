package com.mvo.individualsapi.service;

import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.dto.AccessTokenDto;
import com.mvo.individualsapi.service.impl.RegistrationAndLoginServiceImpl;
import com.mvo.individualsapi.service.keycloak.impl.KeyCloakClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationAndLoginServiceTest {

    @Mock
    private KeyCloakClientImpl keyCloakClientImpl;
    @InjectMocks
    private RegistrationAndLoginServiceImpl service;

    private RegistrationOrLoginRequestDTO testRequestDTO;

    private AccessTokenDto expectedResponse;

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        testRequestDTO = new RegistrationOrLoginRequestDTO().toBuilder()
                .email("test@test.com")
                .password("password")
                .confirmPassword("password")
                .build();

        expectedResponse = new AccessTokenDto().toBuilder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .build();
    }

    @Test
    void testRegistrationUser_Success() {
        String adminTokenTest = "admin-token-test";
        when(keyCloakClientImpl.createUser(any(RegistrationOrLoginRequestDTO.class), any(String.class))).thenReturn(Mono.empty());
        when(keyCloakClientImpl.getAdminToken()).thenReturn(Mono.just(adminTokenTest));
        when(keyCloakClientImpl.getToken(any(String.class), any(String.class))).thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(service.registrationUser(testRequestDTO))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void testLoginUser_Success() {
        when(keyCloakClientImpl.getToken(any(String.class), any(String.class))).thenReturn(Mono.just(expectedResponse));
        StepVerifier.create(service.loginUser(testRequestDTO))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}