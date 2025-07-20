package org.homeplant.model;


import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "feedbacks")
public class Feedback {

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private UUID id;

private String userName;
private String userEmail;
@Column(name = "user_mobile_number", nullable = false, unique = true)
private String userMobileNumber;

@Column(name = "user_text", length = 2000)
private String userRequestText;

@Column(name = "created_at", updatable = false)
@CreationTimestamp
private LocalDateTime createdAt;

public Feedback(String userName, String userEmail, String userMobileNumber, String userRequestText) {
    this.userName = userName;
    this.userEmail = userEmail;
    this.userMobileNumber = userMobileNumber;
    this.userRequestText = userRequestText;
}

public Feedback() {
}

public String getUserName() {
    return userName;
}

public void setUserName(String userName) {
    this.userName = userName;
}

public String getUserEmail() {
    return userEmail;
}

public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
}

public String getUserMobileNumber() {
    return userMobileNumber;
}

public void setUserMobileNumber(String userMobileNumber) {
    this.userMobileNumber = userMobileNumber;
}

public String getUserRequestText() {
    return userRequestText;
}

public void setUserRequestText(String userRequestText) {
    this.userRequestText = userRequestText;
}

public UUID getId() {
    return id;
}

public LocalDateTime getCreatedAt() {
    return createdAt;
}

@Override
public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Feedback feedback = (Feedback) o;
    return Objects.equals(id, feedback.id) && Objects.equals(userName, feedback.userName) && Objects.equals(userEmail, feedback.userEmail) && Objects.equals(userMobileNumber, feedback.userMobileNumber) && Objects.equals(userRequestText, feedback.userRequestText);
}

@Override
public int hashCode() {
    return Objects.hash(id, userName, userEmail, userMobileNumber, userRequestText);
}

@Override
public String toString() {
    return "Feedback{" +
            "id=" + id +
            ", userName='" + userName + '\'' +
            ", userEmail='" + userEmail + '\'' +
            ", userMobileNumber='" + userMobileNumber + '\'' +
            ", userRequestText='" + userRequestText + '\'' +
            '}';
}
}