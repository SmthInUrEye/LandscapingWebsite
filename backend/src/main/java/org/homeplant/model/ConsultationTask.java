package org.homeplant.model;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "consultation_tasks")
public class ConsultationTask {

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private UUID id;

@Column(nullable = false)
private String userName;

@Column(name = "user_mobile_number", nullable = false, unique = true)
private String userMobileNumber;

public ConsultationTask() {
}

public ConsultationTask(String userName, String userMobileNumber) {
    this.userName = userName;
    setUserMobileNumber(userMobileNumber);
}

public UUID getId() {
    return id;
}

public String getUserName() {
    return userName;
}

public void setUserName(String userName) {
    this.userName = Objects.requireNonNull(userName, "Имя пользователя не может быть null");
}

public String getUserMobileNumber() {
    return userMobileNumber;
}

public String getUserMobileNumberFormatted() {
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    try {
        Phonenumber.PhoneNumber number = phoneUtil.parse(userMobileNumber, "RU");
        return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
    } catch (Exception e) {
        return userMobileNumber;
    }
}

public void setUserMobileNumber(String rawNumber) {
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    try {
        Phonenumber.PhoneNumber number = phoneUtil.parse(
         Objects.requireNonNull(rawNumber, "Номер телефона не может быть null"),
         "RU"
        );

        if (!phoneUtil.isValidNumber(number)) {
            throw new IllegalArgumentException("Номер телефона недействителен");
        }

        this.userMobileNumber = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
    } catch (Exception e) {
        throw new IllegalArgumentException("Неверный формат номера: " + e.getMessage());
    }
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ConsultationTask that)) return false;
    return Objects.equals(id, that.id) &&
            Objects.equals(userName, that.userName) &&
            Objects.equals(userMobileNumber, that.userMobileNumber);
}

@Override
public int hashCode() {
    return Objects.hash(id, userName, userMobileNumber);
}

@Override
public String toString() {
    return "ConsultationTask{" +
            "id=" + id +
            ", userName='" + userName + '\'' +
            ", userMobileNumber='" + userMobileNumber + '\'' +
            '}';
}
}