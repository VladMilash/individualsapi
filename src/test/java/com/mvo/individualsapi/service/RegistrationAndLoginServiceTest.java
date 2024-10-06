package com.mvo.individualsapi.service;

import com.mvo.individualsapi.dto.RegistrationOrLoginRequestDTO;
import com.mvo.individualsapi.dto.RegistrationOrLoginResponseDTO;
import com.mvo.individualsapi.service.impl.RegistrationAndLoginServiceImpl;
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

import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationAndLoginServiceTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private RegistrationAndLoginServiceImpl service;

    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    private WebClient.RequestBodySpec requestBodySpec;
    private WebClient.ResponseSpec responseSpec;
    private RegistrationOrLoginRequestDTO testRequestDTO;
    private RegistrationOrLoginResponseDTO expectedResponse;

    @BeforeEach
    void setUp() {
        setupTestData();
        setupWebClientMocks();
        setupServiceProperties();
    }

    private void setupTestData() {
        testRequestDTO = new RegistrationOrLoginRequestDTO().toBuilder()
                .email("test@test.com")
                .password("password")
                .confirmPassword("password")
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
        ReflectionTestUtils.setField(service, "keycloakIssuerUri", "http://keycloak");
        ReflectionTestUtils.setField(service, "clientId", "test-client");
        ReflectionTestUtils.setField(service, "clientSecret", "test-secret");
        ReflectionTestUtils.setField(service, "authorizationUri", "http://auth");
    }

    @Test
    void testRegistrationUser_Success() {
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.exchangeToMono(any())).thenReturn(Mono.empty());
        when(responseSpec.bodyToMono(Map.class))
                .thenReturn(Mono.just(Map.of("access_token", "admin-token")));
        when(responseSpec.bodyToMono(RegistrationOrLoginResponseDTO.class))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(service.registrationUser(testRequestDTO))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void testLoginUser_Success() {
        when(responseSpec.bodyToMono(RegistrationOrLoginResponseDTO.class))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(service.loginUser(testRequestDTO))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}