package com.example.HealthCare.controller.user;

import com.example.HealthCare.domain.dto.request.response.StandardResponse;
import com.example.HealthCare.domain.dto.request.user.UserDetailsForFront;
import com.example.HealthCare.domain.dto.request.user.UserUpdateRequest;
import com.example.HealthCare.domain.entity.user.UserEntity;
import com.example.HealthCare.exception.RequestValidationException;
import com.example.HealthCare.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    @PutMapping("/update-user")
    public StandardResponse<UserDetailsForFront> updateUpdateProfile(
            @Valid @RequestBody UserUpdateRequest update,
            BindingResult bindingResult,
            Principal principal
    )throws RequestValidationException {
        if (bindingResult.hasErrors()){
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            throw new RequestValidationException(allErrors);
        }
        return userService.updateProfile(update, principal);
    }

    @GetMapping("/get-all-user")
    public StandardResponse<List<UserEntity>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return userService.getAll(page, size);
    }

    @GetMapping("/get-me")
    public StandardResponse<UserDetailsForFront> getMe(
            Principal principal
    ){
        return userService.getMeByToken(principal.getName());
    }
}
