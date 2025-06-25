package org.homeplant.model;

import jakarta.validation.constraints.NotBlank;

public class ConsultationTaskRequest {

    @NotBlank(message = "Имя обязательно")
    private String userName;

    @NotBlank(message = "Номер телефона обязателен")
    private String rawPhoneNumber;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRawPhoneNumber() {
        return rawPhoneNumber;
    }

    public void setRawPhoneNumber(String rawPhoneNumber) {
        this.rawPhoneNumber = rawPhoneNumber;
    }
}