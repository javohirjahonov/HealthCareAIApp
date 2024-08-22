package com.example.HealthCare.bot.entity;

import com.example.HealthCare.bot.entity.enums.Language;
import jakarta.persistence.*;
import lombok.*;
import com.example.HealthCare.bot.entity.enums.UserRole;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@SuppressWarnings("all")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Long chatId;
    private boolean isDeleted;
    @Enumerated(EnumType.STRING)
    private Language language;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
}