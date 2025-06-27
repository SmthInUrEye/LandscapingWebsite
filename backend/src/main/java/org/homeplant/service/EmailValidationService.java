package org.homeplant.service;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailValidationService {
    private final EmailValidator emailValidator;

    public EmailValidationService() {
        this.emailValidator = EmailValidator.getInstance(true, true);
    }

    public boolean isValid(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        return emailValidator.isValid(email);
    }

    public String normalizeAndValidate(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }

        String normalizedEmail = email.trim().toLowerCase();
        return emailValidator.isValid(normalizedEmail) ? normalizedEmail : null;
    }

    public void validateEmailOrThrow(String email) {
        if (!isValid(email)) {
            throw new IllegalArgumentException("Неверный формат email: " + email);
        }
    }
}


