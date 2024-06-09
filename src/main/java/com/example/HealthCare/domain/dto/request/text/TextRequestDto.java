package com.example.HealthCare.domain.dto.request.text;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TextRequestDto {
    @NotBlank(message = "text must not be blank")
    private String text;
}
