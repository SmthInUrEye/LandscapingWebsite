package org.homeplant.model;

import jakarta.persistence.*;

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
        this.userMobileNumber = userMobileNumber;
    }

    public UUID getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }
}