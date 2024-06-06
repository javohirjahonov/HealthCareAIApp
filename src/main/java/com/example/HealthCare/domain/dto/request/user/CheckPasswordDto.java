package com.example.HealthCare.domain.dto.request.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CheckPasswordDto {
    private String password;
}
