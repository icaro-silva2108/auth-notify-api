package com.icaro.auth_notify.user.repository;

import com.icaro.auth_notify.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}