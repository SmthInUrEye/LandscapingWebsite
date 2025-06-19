package org.homeplant.model;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.validation.constraints.NotBlank;

public class ConsultationTaskRequest{

@NotBlank(message="Имя обязательно")
private String userName;

@NotBlank(message="Номер телефона обязателен")
private String rawPhoneNumber;

private final String defaultRegion; // Для номеров без кода страны

public ConsultationTaskRequest(String userName,String rawPhoneNumber){
    this.userName=userName;
    this.rawPhoneNumber=rawPhoneNumber;
    this.defaultRegion = "RU";
}

public String getUserName(){
    return userName;
}

public void setUserName(String userName){
    this.userName=userName!=null?userName.trim():null;
}

public String getRawPhoneNumber(){
    return rawPhoneNumber;
}

public void setRawPhoneNumber(String rawPhoneNumber){
    this.rawPhoneNumber=rawPhoneNumber;
}

public String getNormalizedPhoneNumber(){
    PhoneNumberUtil phoneUtil=PhoneNumberUtil.getInstance();
    try{

        Phonenumber.PhoneNumber number=phoneUtil.parseAndKeepRawInput(rawPhoneNumber,defaultRegion);

        if(!phoneUtil.isValidNumber(number)){
            throw new IllegalArgumentException("Недействительный номер для региона "+phoneUtil.getRegionCodeForNumber(number));
        }

        return phoneUtil.format(number,PhoneNumberUtil.PhoneNumberFormat.E164);

    }catch(NumberParseException e){
        throw new IllegalArgumentException(getErrorMessage(e.getErrorType()));
    }
}

private String getErrorMessage(NumberParseException.ErrorType errorType){
    return switch(errorType){
        case INVALID_COUNTRY_CODE -> "Неверный код страны";
        case NOT_A_NUMBER -> "Это не похоже на номер телефона";
        case TOO_SHORT_AFTER_IDD -> "Слишком короткий номер после международного префикса";
        case TOO_SHORT_NSN -> "Слишком короткий номер";
        case TOO_LONG -> "Слишком длинный номер";
        default -> "Неверный формат номера. Примеры: +79123456789, 89123456789, 8 (912) 345-67-89";
    };
}

}