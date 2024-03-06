package com.example.HealthCare.repository;

import com.example.HealthCare.domain.entity.image.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ImageDataRepository extends JpaRepository<ImageEntity, UUID> {
}
