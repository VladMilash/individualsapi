package com.mvo.individualsapi.errorhandling;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import com.mvo.individualsapi.exception.ApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AppErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        var errorAttributes = super.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        var error = getError(request);

        var errorList = new ArrayList<Map<String, Object>>();
        HttpStatus status;

        if (error instanceof IllegalArgumentException || error instanceof IllegalStateException) {
            status = HttpStatus.BAD_REQUEST;
            errorList.add(createErrorMap("BAD_REQUEST", error.getMessage()));
        } else if (error instanceof SecurityException || error instanceof org.springframework.security.core.AuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
            errorList.add(createErrorMap("UNAUTHORIZED", "Authentication failed or not provided"));
        } else if (error instanceof org.springframework.web.server.ResponseStatusException responseStatusException) {
            status = HttpStatus.valueOf(responseStatusException.getStatusCode().value());
            errorList.add(createErrorMap(status.name(), responseStatusException.getReason()));
        } else if (error instanceof ApiException apiException) {
            status = HttpStatus.BAD_REQUEST;
            errorList.add(createErrorMap(apiException.getErrorCode(), apiException.getMessage()));
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            var message = error.getMessage();
            if (message == null)
                message = "An unexpected error occurred";
            errorList.add(createErrorMap("INTERNAL_ERROR", message));
        }

        var errors = new HashMap<String, Object>();
        errors.put("errors", errorList);

        errorAttributes.put("status", status.value());
        errorAttributes.put("errors", errors);
        return errorAttributes;
    }

    private Map<String, Object> createErrorMap(String code, String message) {
        var errorMap = new LinkedHashMap<String, Object>();
        errorMap.put("code", code);
        errorMap.put("message", message);
        return errorMap;
    }
}
