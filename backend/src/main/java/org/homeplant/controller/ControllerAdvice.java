package org.homeplant.controller;

import org.homeplant.exception.DuplicatePhoneException;
import org.homeplant.exception.EntityNotFoundException;
import org.homeplant.exception.InvalidPhoneException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
@ExceptionHandler(InvalidPhoneException.class)
public ResponseEntity<String> handleInvalidPhone(InvalidPhoneException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
}

@ExceptionHandler(DuplicatePhoneException.class)
public ResponseEntity<String> handleDuplicatePhone(DuplicatePhoneException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
}

@ExceptionHandler(EntityNotFoundException.class)
public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
}
}