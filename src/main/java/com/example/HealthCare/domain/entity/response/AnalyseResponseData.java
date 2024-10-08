package com.example.HealthCare.domain.entity.response;

import com.example.HealthCare.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "response_data")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AnalyseResponseData extends BaseEntity {
    private UUID userId;
    private String responseData;
    private LocalDateTime responseTime;
}
