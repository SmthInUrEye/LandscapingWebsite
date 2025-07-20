package org.homeplant.service;

import org.homeplant.exception.DuplicatePhoneException;
import org.homeplant.exception.EntityNotFoundException;
import org.homeplant.exception.InvalidPhoneException;
import org.homeplant.model.ConsultationTask;
import org.homeplant.model.ConsultationTaskRequest;
import org.homeplant.repository.ConsultationTaskRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConsultationTaskService {

private final ConsultationTaskRepository repository;
private final PhoneValidationService phoneValidationService;
private final ApplicationEventPublisher eventPublisher;
private final Object lock = new Object();

public ConsultationTaskService(
 ConsultationTaskRepository repository,
 PhoneValidationService phoneValidationService, ApplicationEventPublisher eventPublisher
) {
    this.repository = repository;
    this.phoneValidationService = phoneValidationService;
    this.eventPublisher = eventPublisher;
}

@Transactional
public ConsultationTask createTask(ConsultationTaskRequest request) {
    try {
        // Валидация и нормализация номера
        String normalizedPhone = phoneValidationService.validateAndNormalize(
         request.getRawPhoneNumber()
        );

        // Проверка уникальности
        if (repository.existsByUserMobileNumber(normalizedPhone)) {
            throw new DuplicatePhoneException("Заявка уже оставлена и обрабатывается!");
        }

        // Создание сущности
        ConsultationTask task = new ConsultationTask(
         request.getUserName().trim(),
         normalizedPhone
        );

        synchronized (lock) {
            eventPublisher.publishEvent(task);
        }

        return repository.save(task);

    } catch (IllegalArgumentException ex) {
        throw new InvalidPhoneException(ex.getMessage());
    } catch (DataIntegrityViolationException ex) {
        throw new DuplicatePhoneException("Заявка уже оставлена и обрабатывается!", ex);
    }
}

@Transactional(readOnly = true)
public String getFormattedPhone(UUID taskId) {
    return repository.findById(taskId)
            .map(task -> phoneValidationService.formatInternational(
             task.getUserMobileNumber()
            ))
            .orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));
}

@Transactional(readOnly = true)
public Optional<ConsultationTask> findById(UUID id) {
    return repository.findById(id);
}

@Transactional(readOnly = true)
public List<ConsultationTask> findAll() {
    return repository.findAll();
}

@Transactional
public void deleteTask(UUID id) {
    repository.deleteById(id);
}
}