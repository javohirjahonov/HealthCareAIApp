package com.example.HealthCare.bot.service.base;


import com.example.HealthCare.bot.entity.Feedback;

public interface FeedbackService {
    void save(Feedback feedback);

    Feedback getById(Long feedbackId);

    void deleteById(Long feedbackId);
}
