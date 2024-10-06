package com.mvo.individualsapi.service;

import com.mvo.individualsapi.dto.UserinfoResponseDTO;
import com.mvo.individualsapi.service.impl.UserinfoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserinfoServiceTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private UserinfoServiceImpl service;

    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;
    private UserinfoResponseDTO expectedResponse;

    @BeforeEach
    void setUp() {
        setupTestData();
        setupWebClientMocks();
        setupServiceProperties();
    }

    private void setupTestData() {
        expectedResponse = new UserinfoResponseDTO();
    }

    private void setupWebClientMocks() {
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    private void setupServiceProperties() {
        ReflectionTestUtils.setField(service, "keycloakIssuerUri", "http://keycloak");
    }

    @Test
    void testGetUserinfo_Success() {
        when(responseSpec.bodyToMono(UserinfoResponseDTO.class))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(service.getUserinfo("test-token"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}