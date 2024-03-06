package com.example.HealthCare.domain.entity.image;

import com.example.HealthCare.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.*;

@Entity(name = "images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageEntity extends BaseEntity {
    @Lob
    private byte[] imageData;

}
