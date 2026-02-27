package com.bosams.common;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String,Object>> handle(ApiException e){
        return ResponseEntity.status(e.getStatus()).body(Map.of("code",e.getCode(),"message",e.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> val(MethodArgumentNotValidException e){
        return ResponseEntity.badRequest().body(Map.of("code","VALIDATION_ERROR","message","Validation failed","details",e.getBindingResult().toString()));
    }
}
