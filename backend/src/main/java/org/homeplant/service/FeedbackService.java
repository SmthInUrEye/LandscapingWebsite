package org.homeplant.service;

import org.homeplant.exception.DuplicateEmailException;
import org.homeplant.exception.DuplicatePhoneException;
import org.homeplant.exception.InvalidPhoneException;
import org.homeplant.model.Feedback;
import org.homeplant.model.FeedbackRequest;
import org.homeplant.repository.FeedbackRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FeedbackService {

    private final FeedbackRepository repository;
    private final PhoneValidationService phoneValidationService;
    private final EmailValidationService emailValidationService;
    private final ApplicationEventPublisher eventPublisher;

    public FeedbackService(FeedbackRepository repository, PhoneValidationService phoneValidationService, EmailValidationService emailValidationService, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.phoneValidationService = phoneValidationService;
        this.emailValidationService = emailValidationService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Feedback createFeedback(FeedbackRequest request) {
        try {

            // Валидация и нормализация номера
            String normalizedPhone = phoneValidationService.validateAndNormalize(
             request.getRawPhoneNumber()
            );

            // Проверка уникальности телефона
            if (repository.existsByUserMobileNumber(normalizedPhone)) {
                throw new DuplicatePhoneException("Заявка уже оставлена и обрабатывается!");
            }

            // Валидация и нормализация Email
            String normalizedEmail = Optional.ofNullable(request.getRawEmail())
             .filter(email -> !email.isBlank())
             .map(email -> emailValidationService.normalizeAndValidate(email))
             .orElse(null);

            // Проверка уникальности Email
            if (normalizedEmail != null && repository.existsByUserEmail(normalizedEmail)) {
                throw new DuplicateEmailException("Заявка уже оставлена и обрабатывается!");
            }

            // Создание сущности
            Feedback feedback = new Feedback(
             request.getUserName().trim(),
             normalizedEmail,
             normalizedPhone,
             request.getUserRequestText()
            );

            //eventPublisher.publishEvent(feedback);
            return repository.save(feedback);

        } catch (IllegalArgumentException ex) {
            throw new InvalidPhoneException(ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            if (ex.getMessage().contains("user_email")) {
                throw new DuplicateEmailException("Заявка уже оставлена и обрабатывается!", ex);
            } else if (ex.getMessage().contains("user_mobile_number")) {
                throw new DuplicatePhoneException("Заявка уже оставлена и обрабатывается!", ex);
            }
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public Optional<Feedback> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Feedback> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void deleteFeedback(UUID id) {
        repository.deleteById(id);
    }
}
