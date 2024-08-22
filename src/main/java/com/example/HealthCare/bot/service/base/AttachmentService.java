package com.example.HealthCare.bot.service.base;


import com.example.HealthCare.bot.entity.Attachment;
import com.example.HealthCare.bot.entity.User;

public interface AttachmentService {
    Attachment save(String url, User user);

    Attachment getById(Long attachmentId);

    void deleteById(Long attachmentId);
}
