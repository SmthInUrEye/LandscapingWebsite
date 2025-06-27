package org.homeplant.repository;

import org.homeplant.model.ConsultationTask;
import org.homeplant.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    boolean existsByUserMobileNumber(String userMobileNumber);

    boolean existsByUserEmail(String userEmail);
}