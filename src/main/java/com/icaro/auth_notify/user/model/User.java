package com.icaro.auth_notify.user.model;

import com.icaro.auth_notify.user.model.enums.AuthProvider;
import com.icaro.auth_notify.user.model.enums.UserRole;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tb_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Setter
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.USER;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "tb_user_auth_provider",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "provider")
    private Set<AuthProvider> providers = new HashSet<>();

    @Setter
    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Builder
    public User(
            String name,
            String email,
            String passwordHash,
            AuthProvider authProvider,
            LocalDate birthDate
    ) {
        this.name = Objects.requireNonNull(name, "user name should not be null");
        this.email = Objects.requireNonNull(email, "user email should not be null");

        if (passwordHash != null && passwordHash.isBlank()) {
            throw new IllegalArgumentException("user password hash should not be blank");
        }
        this.passwordHash = passwordHash;

        this.birthDate = Objects.requireNonNull(birthDate, "user birth date should not be null");

        if(name.isBlank()) { throw new IllegalArgumentException("user name should not be blank"); }
        if(email.isBlank()) { throw new IllegalArgumentException("user email should not be blank"); }
    }

    // SETTERS

    public void setName(String name) {

        name = Objects.requireNonNull(name, "user name should not be changed to null");
        if(name.isBlank()) { throw new IllegalArgumentException("user name should not be changed to blank"); }

        this.name = name;
    }

    public void setEmail(String email) {

        email = Objects.requireNonNull(email, "user email should not be changed to null");
        if(email.isBlank()) { throw new IllegalArgumentException("user email should not be changed to blank"); }

        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {

        if(passwordHash != null && passwordHash.isBlank()) { throw new IllegalArgumentException("user password hash should not be changed to blank"); }
        this.passwordHash = passwordHash;
    }

    public void setRole(UserRole role) {

        this.role = Objects.requireNonNull(role, "user role should not be changed to null");
    }

    // USER DETAILS

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(("ROLE_" + role.name()))
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}