package org.homeplant.controller;

import jakarta.validation.Valid;
import org.homeplant.model.ConsultationTask;
import org.homeplant.model.ConsultationTaskRequest;
import org.homeplant.service.ConsultationTaksService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController

@RequestMapping("/api/consultation-tasks")
public class ConsultationTaskController{

private final ConsultationTaksService service;

public ConsultationTaskController(ConsultationTaksService service){
    this.service=service;
}

@PostMapping
public ResponseEntity<?> createTask(@Valid @RequestBody ConsultationTaskRequest request) {
    try {
        ConsultationTask task = service.createTask(request);
        return ResponseEntity.ok(task);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


@GetMapping("/{id}")
public ResponseEntity<ConsultationTask> getTask(@PathVariable UUID id){
    return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
}

@GetMapping
public List<ConsultationTask> getAllTasks(){
    return service.findAll();
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteTask(@PathVariable UUID id){
    service.deleteTask(id);
    return ResponseEntity.noContent().build();
}
}