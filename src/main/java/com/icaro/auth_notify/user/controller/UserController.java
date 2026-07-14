package com.icaro.auth_notify.user.controller;

import com.icaro.auth_notify.user.model.User;
import com.icaro.auth_notify.user.model.dto.*;
import com.icaro.auth_notify.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager manager;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody
            @Valid UserRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> userLogin(
            @RequestBody @Valid LoginRequestDTO dto) {

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authenticated = manager.authenticate(authToken);

        User user = (User) authenticated.getPrincipal();

        return ResponseEntity.ok(
                new LoginResponseDTO(user.getId(), user.getEmail())
        );
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @RequestBody @Valid UserUpdateDTO dto,
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