package com.example.HealthCare.controller.userAnalyseResponseHistoryController;

import com.example.HealthCare.domain.entity.response.AnalyseResponseData;
import com.example.HealthCare.service.AnalyseResponseDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/userAnalyseResponseHistory")
@RequiredArgsConstructor
public class AnalyseResponseHistoryController {

    private final AnalyseResponseDataService analyseResponseDataService;

    @GetMapping("/history")
    public List<AnalyseResponseData> getUserAnalyseResponseHistory(@RequestParam UUID userId) {
        return analyseResponseDataService.getUserAnalyseResponseData(userId);
    }
}
