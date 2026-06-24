package com.icaro.auth_notify.user.controller;

import com.icaro.auth_notify.user.model.User;
import com.icaro.auth_notify.user.model.dto.UserRequestDTO;
import com.icaro.auth_notify.user.model.dto.UserResponseDTO;
import com.icaro.auth_notify.user.model.dto.UserUpdateDTO;
import com.icaro.auth_notify.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody UserRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(dto));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @RequestBody UserUpdateDTO dto,
            @AuthenticationPrincipal User user) {

        return ResponseEntity
                .ok()
                .body(userService.updateUser(dto, user));
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal User user) {

        userService.deleteUser(user);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponseDTO> me(
            @AuthenticationPrincipal User user) {

        return ResponseEntity
                .ok()
                .body(userService.findUser(user));
    }
}