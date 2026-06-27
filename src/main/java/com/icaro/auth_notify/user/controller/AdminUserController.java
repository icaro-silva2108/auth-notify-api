package com.icaro.auth_notify.user.controller;

import com.icaro.auth_notify.user.model.dto.ChangeRoleDTO;
import com.icaro.auth_notify.user.model.dto.UserResponseDTO;
import com.icaro.auth_notify.user.service.AdminUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> findAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity
                .ok()
                .body(adminUserService.findAllUsers(pageable));
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<UserResponseDTO> findUserById(
            @PathVariable("user-id") Long id) {

        return ResponseEntity
                .ok()
                .body(adminUserService.findUserById(id));
    }

    @PatchMapping("/{user-id}/role")
    public ResponseEntity<UserResponseDTO> changeUserRole(
            @PathVariable("user-id") Long id,
            @RequestBody @Valid ChangeRoleDTO roleDTO) {

        return ResponseEntity
                .ok()
                .body(adminUserService.changeUserRole(id, roleDTO.role()));
    }

    @PatchMapping("/{user-id}/reactivate")
    public ResponseEntity<UserResponseDTO> reactivateUser(
            @PathVariable("user-id") Long id) {

        return ResponseEntity
                .ok()
                .body(adminUserService.reactivateUser(id));
    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity<Void> deleteUserPermanently(
            @PathVariable("user-id") Long id) {

        adminUserService.deleteUserPermanently(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}