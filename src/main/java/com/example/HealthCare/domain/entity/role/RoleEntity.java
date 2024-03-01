package com.example.HealthCare.domain.entity.role;

import com.example.HealthCare.domain.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.util.List;

@Entity(name = "role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleEntity extends BaseEntity {
    private String name;
    @ManyToMany
    @JsonIgnore
    private List<PermissionEntity> permissions;
}
