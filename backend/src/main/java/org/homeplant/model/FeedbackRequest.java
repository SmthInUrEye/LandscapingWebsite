package org.homeplant.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FeedbackRequest {

    @NotBlank(message = "Имя обязательно")
    private String userName;

    @NotBlank(message = "Номер телефона обязателен")
    private String rawPhoneNumber;

    @Email(message = "Некорректный формат email")
    private String rawEmail;

    @Size(max = 2000, message = "Текст обращения слишком длинный (макс. 2000 символов)")
    private String userRequestText;

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

    public String getRawEmail() {
        return rawEmail;
    }

    public void setRawEmail(String rawEmail) {
        this.rawEmail = rawEmail;
    }

    public String getUserRequestText() {
        return userRequestText;
    }

    public void setUserRequestText(String userRequestText) {
        this.userRequestText = userRequestText;
    }
}
