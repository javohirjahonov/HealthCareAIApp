package com.example.HealthCare.service.mail;

import com.example.HealthCare.domain.dto.request.MailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public void sendMessage(MailDto mailDto) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(mailDto.getEmail());
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setText(mailDto.getMessage());
        mailSender.send(simpleMailMessage);
    }

}
