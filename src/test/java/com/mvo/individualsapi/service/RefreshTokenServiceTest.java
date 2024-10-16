package com.mvo.individualsapi.service;

import com.mvo.individualsapi.dto.RefreshTokenRequestDTO;
import com.mvo.individualsapi.dto.AccessTokenDTO;
import com.mvo.individualsapi.service.impl.RefreshTokenServiceImpl;
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
class RefreshTokenServiceTest {
    @Mock
    private KeyCloakClientImpl keyCloakClientImpl;

    @InjectMocks
    private RefreshTokenServiceImpl service;
    private RefreshTokenRequestDTO testRequestDTO;
    private AccessTokenDTO expectedResponse;

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        testRequestDTO = new RefreshTokenRequestDTO().toBuilder()
                .refreshToken("test-refresh-token")
                .build();

        expectedResponse = new AccessTokenDTO().toBuilder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .build();
    }
    
    @Test
    void testRefreshToken_Success() {
        when(keyCloakClientImpl.refreshToken(any(RefreshTokenRequestDTO.class))).thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(service.refreshToken(testRequestDTO))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}