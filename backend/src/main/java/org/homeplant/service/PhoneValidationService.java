package org.homeplant.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.stereotype.Service;

@Service
public class PhoneValidationService {

    private static final String DEFAULT_REGION = "RU";
    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

    public String validateAndNormalize(String rawNumber) {
        if (rawNumber == null || rawNumber.isBlank()) {
            throw new IllegalArgumentException("Номер телефона обязателен");
        }

        try {
            Phonenumber.PhoneNumber number = PHONE_UTIL.parse(rawNumber, DEFAULT_REGION);

            if (!PHONE_UTIL.isValidNumberForRegion(number, DEFAULT_REGION)) {
                String regionCode = PHONE_UTIL.getRegionCodeForNumber(number);
                throw new IllegalArgumentException(
                 "Номер должен быть российским. Обнаружен регион: " +
                  (regionCode != null ? regionCode : "неизвестный")
                );
            }

            return PHONE_UTIL.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);

        } catch (NumberParseException e) {
            throw new IllegalArgumentException(getErrorMessage(e.getErrorType()), e);
        }
    }

    public String formatInternational(String phoneNumber) {
        try {
            return PHONE_UTIL.format(
             PHONE_UTIL.parse(phoneNumber, DEFAULT_REGION),
             PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
            );
        } catch (NumberParseException e) {
            return phoneNumber; // Fallback
        }
    }

    private String getErrorMessage(NumberParseException.ErrorType errorType) {
        return switch (errorType) {
            case INVALID_COUNTRY_CODE -> "Неверный код страны. Пример: +7 или 8";
            case NOT_A_NUMBER -> "Неверный формат номера. Должен содержать цифры";
            case TOO_SHORT_AFTER_IDD -> "Слишком короткий номер после международного префикса";
            case TOO_SHORT_NSN -> "Номер слишком короткий. Минимум 10 цифр";
            case TOO_LONG -> "Номер слишком длинный. Максимум 15 цифр";
            default -> "Неверный формат номера. Примеры: +79123456789, 89123456789, 8 (912) 345-67-89";
        };
    }
}