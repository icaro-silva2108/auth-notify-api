package com.icaro.auth_notify.user.service;

import com.icaro.auth_notify.common.exceptions.ResourceNotFoundException;
import com.icaro.auth_notify.user.model.User;
import com.icaro.auth_notify.user.model.dto.UserResponseDTO;
import com.icaro.auth_notify.user.model.enums.UserRole;
import com.icaro.auth_notify.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;

    // UTILITIES

    private UserResponseDTO toResponseDTO(User user) {

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBirthDate(),
                user.getRole(),
                user.isActive()
        );
    }

    @Transactional(readOnly = true)
    private User findUserOrThrow(Long id) {

        return userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("user not found")
                );
    }

    // ROLE ADMIN METHODS

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAllUsers(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findUserById(Long id) {

        return toResponseDTO(
                userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("user not found"))
        );
    }

    public UserResponseDTO changeUserRole(Long id, UserRole role) {

        User user = findUserOrThrow(id);

        if(user.getRole().equals(role)) {
            throw new IllegalStateException("user already has this role");
        }

        user.setRole(role);
        userRepository.save(user);
        return toResponseDTO(user);
    }

    public UserResponseDTO reactivateUser(Long id) {

        User user = findUserOrThrow(id);

        if(user.isActive()) {
            throw new IllegalStateException("user is already active");
        }

        user.setActive(true);
        userRepository.save(user);
        return toResponseDTO(user);
    }

    public void deleteUserPermanently(Long id) {

        User user = findUserOrThrow(id);

        if(user.getRole().equals(UserRole.ADMIN)) {
            throw new IllegalStateException("only common users are allowed to be permanently deleted");
        }

        userRepository.delete(user);
    }
}