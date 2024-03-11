package com.example.HealthCare.repository;

import com.example.HealthCare.domain.entity.image.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TextDataFromImage extends JpaRepository<ImageEntity, UUID> {
}
