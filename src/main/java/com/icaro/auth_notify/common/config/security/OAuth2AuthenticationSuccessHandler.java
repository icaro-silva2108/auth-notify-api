package com.icaro.auth_notify.common.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icaro.auth_notify.auth.service.JwtService;
import com.icaro.auth_notify.common.exceptions.AuthenticationError;
import com.icaro.auth_notify.user.model.User;
import com.icaro.auth_notify.user.model.dto.LoginResponseDTO;
import com.icaro.auth_notify.user.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        User localUser = userRepository.findByEmail(oidcUser.getEmail())
                .orElseThrow(AuthenticationError::new);

        String token = jwtService.generateToken(localUser);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                mapper.writeValueAsString(
                        new LoginResponseDTO(
                                localUser.getId(),
                                localUser.getEmail(),
                                token
                        )
                )
        );
    }
}