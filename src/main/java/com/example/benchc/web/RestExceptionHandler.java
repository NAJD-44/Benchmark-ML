package com.example.benchc.web;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> notFound(NoSuchElementException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error","Not Found","message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> badRequest(MethodArgumentNotValidException ex){
        var errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                errors.add(Map.of("field", fe.getField(), "message", fe.getDefaultMessage())));
        return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }
}

