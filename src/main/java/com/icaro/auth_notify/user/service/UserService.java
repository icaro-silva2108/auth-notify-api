package com.icaro.auth_notify.user.service;

import com.icaro.auth_notify.common.exceptions.InvalidPasswordException;
import com.icaro.auth_notify.common.exceptions.ResourceNotFoundException;
import com.icaro.auth_notify.user.model.User;
import com.icaro.auth_notify.user.model.dto.UserRequestDTO;
import com.icaro.auth_notify.user.model.dto.UserResponseDTO;
import com.icaro.auth_notify.user.model.dto.UserUpdateDTO;
import com.icaro.auth_notify.user.repository.UserRepository;
import com.icaro.auth_notify.common.exceptions.InvalidAgeException;
import com.icaro.auth_notify.common.exceptions.EmailAlreadyExistsException;

import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    private boolean isUnderAge(LocalDate birthDate) {
        return birthDate.plusYears(14).isAfter(LocalDate.now());
    }

    // ROLE USER METHODS

    public UserResponseDTO createUser(UserRequestDTO dto) {

        if(userRepository.existsByEmail(dto.email())) { throw new EmailAlreadyExistsException(); }
        if(isUnderAge(dto.birthDate())) { throw new InvalidAgeException(); }

        String passwordHash = dto.password() != null
                ? passwordEncoder.encode(dto.password())
                : null;

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .passwordHash(passwordHash)
                .birthDate(dto.birthDate())
                .build();

        User saved = userRepository.save(user);
        return toResponseDTO(saved);
    }

    public UserResponseDTO updateUser(UserUpdateDTO dto, User user) {

        if(dto.name() != null) {

            user.setName(dto.name());
        }
        if(dto.email() != null) {

            if(!dto.email().equals(user.getEmail())
                && userRepository.existsByEmail(dto.email())) {

                throw new EmailAlreadyExistsException();
            }

            user.setEmail(dto.email());
        }
        if(dto.password() != null) {

            if(dto.password().isBlank()) {
                throw new InvalidPasswordException();
            }
            user.setPasswordHash(passwordEncoder.encode(dto.password()));
        }
        if(dto.birthDate() != null) {
             if(isUnderAge(dto.birthDate())) {
                throw new InvalidAgeException();
            }
            user.setBirthDate(dto.birthDate());
        }

        User saved = userRepository.save(user);
        return toResponseDTO(saved);
    }

    public void deleteUser(User user) {

        if(!user.isActive()) {
            throw new IllegalStateException("user is already inactive");
        }

        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findUser(User user) {

        return toResponseDTO(
                userRepository.findById(user.getId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("user not found")
                        )
        );
    }
}