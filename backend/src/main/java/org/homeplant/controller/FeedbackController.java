package org.homeplant.controller;

import jakarta.validation.Valid;
import org.homeplant.exception.DuplicateEmailException;
import org.homeplant.exception.DuplicatePhoneException;
import org.homeplant.exception.EntityNotFoundException;
import org.homeplant.exception.InvalidPhoneException;
import org.homeplant.model.ConsultationTask;
import org.homeplant.model.Feedback;
import org.homeplant.model.FeedbackRequest;
import org.homeplant.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController

@RequestMapping("/api/feedbacks")
public class FeedbackController {

private final FeedbackService service;

public FeedbackController(FeedbackService service) {
    this.service = service;
}

@PostMapping
public ResponseEntity<?> createFeedback(@Valid @RequestBody FeedbackRequest request) {
    try {
        Feedback feedback = service.createFeedback(request);

        URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(feedback.getId())
                        .toUri();

        return ResponseEntity.created(location).body(feedback);

    } catch (InvalidPhoneException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    } catch (DuplicatePhoneException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    } catch (DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}

}