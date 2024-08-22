package com.example.HealthCare.domain.entity;

import com.example.HealthCare.domain.entity.user.UserEntity;
import com.example.HealthCare.domain.entity.user.UserEntity;
import com.example.HealthCare.domain.entity.user.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity(name = "verification")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VerificationEntity extends BaseEntity{
    @OneToOne
    private UserEntity userId;
    private String code;
}
