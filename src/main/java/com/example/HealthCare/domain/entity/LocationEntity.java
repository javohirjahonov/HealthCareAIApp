package com.example.HealthCare.domain.entity;

import jakarta.persistence.Entity;
import lombok.*;

@Entity(name = "locations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LocationEntity extends BaseEntity {
    private Double latitude;
    private Double longitude;

}
