package com.example.HealthCare.repository;

import com.example.HealthCare.domain.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findUserEntityByPhoneNumber(String phoneNumber);


    @Query(value = "select u from b_users u join u.roles r where r.name = 'USER' and u.id = ?1")
    Optional<UserEntity> getUserById(UUID id);
}