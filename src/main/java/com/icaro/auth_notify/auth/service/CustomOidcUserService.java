package com.icaro.auth_notify.auth.service;

import com.icaro.auth_notify.messaging.dto.UserEventDTO;
import com.icaro.auth_notify.messaging.publisher.UserEventPublisher;
import com.icaro.auth_notify.user.model.User;
import com.icaro.auth_notify.user.model.enums.AuthProvider;
import com.icaro.auth_notify.user.repository.UserRepository;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    private final UserEventPublisher eventPublisher;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        OidcUser oidcUser = super.loadUser(userRequest);

        User localUser = userRepository.findByEmail(oidcUser.getEmail())
                .orElseGet(() ->
                    {
                        User user = User.builder()
                                .name(oidcUser.getFullName())
                                .email(oidcUser.getEmail())
                                .passwordHash(null)
                                .birthDate(null)
                                .build();

                        eventPublisher.publishUserCreated(
                                new UserEventDTO(
                                        user.getName(),
                                        user.getEmail(),
                                        "user.created"
                                )
                        );

                        return userRepository.save(user);
                    }
                );

        localUser.getProviders().add(AuthProvider.GOOGLE);
        userRepository.save(localUser);

        return oidcUser;
    }
}