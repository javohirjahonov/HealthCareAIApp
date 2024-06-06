package com.example.HealthCare.domain.dto.request.response;

import org.springframework.http.HttpStatus;

public record ApiErrorResponse(
        String message,
        HttpStatus httpStatus,
        int code
) {
}
