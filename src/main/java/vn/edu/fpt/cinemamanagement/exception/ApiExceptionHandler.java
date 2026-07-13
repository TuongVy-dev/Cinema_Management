package vn.edu.fpt.cinemamanagement.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            ValidationException exception
    ) {
        return ResponseEntity.unprocessableEntity().body(Map.of(
                "code", "VALIDATION_ERROR",
                "message", "Invalid concession data",
                "fields", exception.getFieldErrors()
        ));
    }
}
