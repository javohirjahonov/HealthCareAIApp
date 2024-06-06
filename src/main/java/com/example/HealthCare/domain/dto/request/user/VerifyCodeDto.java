package com.example.HealthCare.domain.dto.request.user;

import lombok.Data;

@Data
public class VerifyCodeDto {
    private String email;
    private String code;
}
