package org.homeplant.controller;

import jakarta.validation.Valid;
import org.homeplant.exception.DuplicatePhoneException;
import org.homeplant.exception.EntityNotFoundException;
import org.homeplant.exception.InvalidPhoneException;
import org.homeplant.model.ConsultationTask;
import org.homeplant.model.ConsultationTaskRequest;
import org.homeplant.service.ConsultationTaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController

@RequestMapping("/api/consultation-tasks")
public class ConsultationTaskController {

    private final ConsultationTaskService service;

    public ConsultationTaskController(ConsultationTaskService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody ConsultationTaskRequest request) {
        try {
            ConsultationTask task = service.createTask(request);

            URI location = ServletUriComponentsBuilder
             .fromCurrentRequest()
             .path("/{id}")
             .buildAndExpand(task.getId())
             .toUri();

            return ResponseEntity.created(location).body(task);

        } catch (InvalidPhoneException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (DuplicatePhoneException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultationTask> getTask(@PathVariable UUID id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ConsultationTask> getAllTasks() {
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        try {
            service.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}