package com.example.HealthCare.bot.repository;

import com.example.HealthCare.bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository(value = "bot-user-repository")
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByChatId(Long chatId);

    @Query("SELECT u FROM User u WHERE u.userRole = 'ADMIN'")
    List<User> getAdmins();
}
