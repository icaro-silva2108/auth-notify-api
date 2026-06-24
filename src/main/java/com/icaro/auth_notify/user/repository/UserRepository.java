package com.icaro.auth_notify.user.repository;

import com.icaro.auth_notify.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    public boolean existsByEmail(String email);
}