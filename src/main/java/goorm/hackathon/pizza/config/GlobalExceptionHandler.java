//// GlobalExceptionHandler.java
//package goorm.hackathon.pizza.config;
//
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(EntityNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public Map<String, Object> handleNotFound(EntityNotFoundException e) {
//        return Map.of("message", e.getMessage());
//    }
//
//    @ExceptionHandler(IllegalStateException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Map<String, Object> handleBadRequest(IllegalStateException e) {
//        return Map.of("message", e.getMessage());
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public Map<String, Object> handleForbidden(AccessDeniedException e) {
//        return Map.of("message", e.getMessage());
//    }
//}
