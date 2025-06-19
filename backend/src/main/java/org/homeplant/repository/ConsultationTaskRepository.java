package org.homeplant.repository;

import org.homeplant.model.ConsultationTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ConsultationTaskRepository extends JpaRepository<ConsultationTask, UUID> {
boolean existsByUserMobileNumber(String userMobileNumber);
}