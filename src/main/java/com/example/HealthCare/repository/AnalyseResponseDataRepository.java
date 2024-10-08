package com.example.HealthCare.repository;

import com.example.HealthCare.domain.entity.response.AnalyseResponseData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnalyseResponseDataRepository extends JpaRepository<AnalyseResponseData, UUID> {
    List<AnalyseResponseData> findByUserIdOrderByResponseTime(UUID userId);
}
