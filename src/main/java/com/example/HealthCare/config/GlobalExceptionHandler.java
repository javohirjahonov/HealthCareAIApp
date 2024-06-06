package com.example.HealthCare.config;

import com.example.HealthCare.domain.dto.request.response.ApiErrorResponse;
import com.example.HealthCare.domain.dto.request.response.StandardResponse;
import com.example.HealthCare.domain.dto.request.response.Status;
import com.example.HealthCare.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {RequestValidationException.class})
    public ResponseEntity<StandardResponse<String>>
    requestValidationExceptionHandler(
            RequestValidationException e
    ) {
        return ResponseEntity.status(400).body(StandardResponse.<String>builder().status(Status.ERROR).message(e.getMessage()).build());
    }

    @ExceptionHandler(value = {AuthenticationFailedException.class})
    public ResponseEntity<StandardResponse<String>> authenticationFailedExceptionHandler(
            AuthenticationFailedException e
    ) {
        return ResponseEntity.status(401).body(StandardResponse.<String>builder().status(Status.ERROR).message(e.getMessage()).build());
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<StandardResponse<String>> AccessDeniedExceptionHandler(
            AccessDeniedException e
    ) {
        return ResponseEntity.status(403).body(StandardResponse.<String>builder().status(Status.ERROR).message(e.getMessage()).build());
    }

    @ExceptionHandler(value = {DataNotFoundException.class})
    public ResponseEntity<StandardResponse<String>> dataNotFoundExceptionHandler(
            DataNotFoundException e) {
        return ResponseEntity.status(404).body(StandardResponse.<String>builder().status(Status.ERROR).message(e.getMessage()).build());

    }

    @ExceptionHandler(value = {UniqueObjectException.class})
    public ResponseEntity<StandardResponse<String>> uniqueObjectExceptionHandler(
            UniqueObjectException e) {
        return ResponseEntity.status(401).body(StandardResponse.<String>builder().status(Status.ERROR).message(e.getMessage()).build());
    }

    @ExceptionHandler(value = {UserBadRequestException.class})
    public ResponseEntity<StandardResponse<String>> userBadRequestExceptionHandler(
            UserBadRequestException e) {
        return ResponseEntity.status(400).body(StandardResponse.<String>builder().status(Status.ERROR).message(e.getMessage()).build());
    }
//    @ExceptionHandler(value = {InternalServerErrorException.class})
//    public ResponseEntity<StandardResponse<String>> internalServerExceptionHandler(
//    ){
//        return ResponseEntity.status(401).body(StandardResponse.<String>builder().status(Status.ERROR).message("Data not exist").build());
//
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = Objects.requireNonNull(e.getBindingResult().getFieldError().getDefaultMessage());
        return ResponseEntity.status(400).body(
                new ApiErrorResponse(errorMessage, HttpStatus.BAD_REQUEST, 400)
        );
    }
}
