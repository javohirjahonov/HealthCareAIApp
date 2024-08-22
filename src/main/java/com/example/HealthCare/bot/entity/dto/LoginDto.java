package com.example.HealthCare.bot.entity.dto;

import com.example.HealthCare.bot.entity.enums.Language;
import com.example.HealthCare.bot.entity.enums.UserRole;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    private String phoneNumber;
    private Long chatId;
    private String firstName;
    private String lastName;
    private Language language;
    private UserRole userRole;
}