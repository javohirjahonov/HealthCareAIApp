package com.example.HealthCare.config;

import com.example.HealthCare.domain.entity.user.UserEntity;
import com.example.HealthCare.exception.DataNotFoundException;
import com.example.HealthCare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository authUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity authUser = authUserRepository.findByEmail(username)
                .orElseThrow(() -> new DataNotFoundException("User not found with email: " + username));

        return authUser;
    }
}
