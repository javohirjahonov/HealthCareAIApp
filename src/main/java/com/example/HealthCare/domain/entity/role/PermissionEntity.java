package com.example.HealthCare.domain.entity.role;

import com.example.HealthCare.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity(name = "permission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionEntity extends BaseEntity {
    private String permission;
}
