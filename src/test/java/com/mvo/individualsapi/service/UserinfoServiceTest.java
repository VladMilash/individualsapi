package com.mvo.individualsapi.service;

import com.mvo.individualsapi.dto.UserinfoResponseDTO;
import com.mvo.individualsapi.service.impl.UserinfoServiceImpl;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserinfoServiceTest {

    @Mock
    private KeyCloakClientImpl keyCloakClientImpl;

    @InjectMocks
    private UserinfoServiceImpl service;
    private UserinfoResponseDTO expectedResponse;

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        expectedResponse = UserinfoResponseDTO.builder()
                .email("test")
                .mailVerified("test")
                .sub("test")
                .preferredUsername("test")
                .build();
    }

    @Test
    void testGetUserinfo_Success() {
        when(keyCloakClientImpl.getUserinfo(any(String.class))).thenReturn(Mono.just(expectedResponse));
        StepVerifier.create(service.getUserinfo("token"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}