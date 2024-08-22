package com.example.HealthCare.bot.service.base;



import com.example.HealthCare.bot.entity.User;
import com.example.HealthCare.bot.entity.dto.LoginDto;

import java.util.List;

public interface UserService {
    User getByChatId(Long chatId);

    void login(LoginDto loginDto);

    void save(User user);

    void deleteById(Long userId);

    String aboutMe(User user);

    List<User> getAdmins();
}
