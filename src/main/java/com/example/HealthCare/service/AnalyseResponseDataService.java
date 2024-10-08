package com.example.HealthCare.service;

import com.example.HealthCare.domain.entity.response.AnalyseResponseData;
import com.example.HealthCare.repository.AnalyseResponseDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyseResponseDataService {
    private final AnalyseResponseDataRepository analyseResponseDataRepository;

    public AnalyseResponseData saveAnalyseResponseData(UUID userId, String analyseResponseData) {
        AnalyseResponseData analyseResponseData1 = new AnalyseResponseData(userId, analyseResponseData, LocalDateTime.now());
        return analyseResponseDataRepository.save(analyseResponseData1);
    }

    public List<AnalyseResponseData> getUserAnalyseResponseData(UUID userId) {
        return analyseResponseDataRepository.findByUserIdOrderByResponseTime(userId);
    }
}
