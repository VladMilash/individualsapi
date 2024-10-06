package com.mvo.individualsapi.service;

import com.mvo.individualsapi.dto.RefreshTokenRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginResponseDTO;
import com.mvo.individualsapi.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private RefreshTokenServiceImpl service;

    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    private WebClient.RequestBodySpec requestBodySpec;
    private WebClient.ResponseSpec responseSpec;
    private RefreshTokenRequestDTO testRequestDTO;
    private RegistrationOrLoginResponseDTO expectedResponse;

    @BeforeEach
    void setUp() {
        setupTestData();
        setupWebClientMocks();
        setupServiceProperties();
    }

    private void setupTestData() {
        testRequestDTO = new RefreshTokenRequestDTO().toBuilder()
                .refreshToken("test-refresh-token")
                .build();

        expectedResponse = new RegistrationOrLoginResponseDTO().toBuilder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .build();
    }

    private void setupWebClientMocks() {
        requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        requestBodySpec = mock(WebClient.RequestBodySpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(BodyInserter.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    }

    private void setupServiceProperties() {
        ReflectionTestUtils.setField(service, "clientId", "test-client");
        ReflectionTestUtils.setField(service, "clientSecret", "test-secret");
        ReflectionTestUtils.setField(service, "authorizationUri", "http://auth");
    }

    @Test
    void testRefreshToken_Success() {
        when(responseSpec.bodyToMono(RegistrationOrLoginResponseDTO.class))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(service.refreshToken(testRequestDTO))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}