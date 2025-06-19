package org.homeplant.service;

import org.homeplant.model.ConsultationTask;
import org.homeplant.model.ConsultationTaskRequest;
import org.homeplant.repository.ConsultationTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConsultationTaksService {

private final ConsultationTaskRepository repository;

public ConsultationTaksService(ConsultationTaskRepository repository){
    this.repository=repository;
}

@Transactional
public ConsultationTask createTask(ConsultationTaskRequest request) {
    // Нормализация и валидация номера
    String normalizedPhone = request.getNormalizedPhoneNumber();
    // Проверка уникальности
    if (repository.existsByUserMobileNumber(normalizedPhone)) {
        throw new IllegalArgumentException("Номер " + normalizedPhone + " уже используется");
    }

    // Создание и сохранение
    ConsultationTask task = new ConsultationTask();
    task.setUserName(request.getUserName());
    task.setUserMobileNumber(normalizedPhone);

    return repository.save(task);
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

@Transactional(readOnly = true)
public boolean existsByPhoneNumber(String phoneNumber) {
    return repository.existsByUserMobileNumber(phoneNumber);
}
}
