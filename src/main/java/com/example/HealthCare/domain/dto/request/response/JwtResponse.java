package com.example.HealthCare.domain.dto.request.response;

import com.example.HealthCare.domain.dto.request.user.UserDetailsForFront;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private UserDetailsForFront user;
}
