package com.mvo.individualsapi.service;

import com.mvo.individualsapi.dto.UserinfoResponseDTO;
import reactor.core.publisher.Mono;

public interface UserinfoService {
    Mono<UserinfoResponseDTO> getUserinfo(String token);
}
