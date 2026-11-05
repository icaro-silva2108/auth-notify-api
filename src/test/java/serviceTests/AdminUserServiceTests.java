package serviceTests;

import com.icaro.auth_notify.messaging.publisher.UserEventPublisher;
import com.icaro.auth_notify.messaging.dto.UserEventMessageDTO;
import com.icaro.auth_notify.user.repository.UserRepository;
import com.icaro.auth_notify.user.model.dto.UserResponseDTO;
import com.icaro.auth_notify.messaging.enums.UserEventType;
import com.icaro.auth_notify.user.service.AdminUserService;
import com.icaro.auth_notify.user.model.enums.UserRole;
import com.icaro.auth_notify.user.model.User;

import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminUserServiceTests {

    @Mock
    UserRepository userRepository;

    @Mock
    UserEventPublisher eventPublisher;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    AdminUserService adminUserService;

    // SETUP

    User user;

    @BeforeEach
    void setup() {

        user = User.builder()
                .name("test")
                .email("test@test.com")
                .passwordHash("passwordHash")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();

        ReflectionTestUtils.setField(user, "id", 1L);
    }

    // USER ROLE CHANGED TEST METHODS

    @Test
    void changeUserRoleSuccessTest() {

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response = adminUserService.changeUserRole(user.getId(), UserRole.ADMIN);

        assertEquals(1L, response.id());
        assertEquals(UserRole.ADMIN, response.role());

        ArgumentCaptor<UserEventMessageDTO> eventCaptor = ArgumentCaptor.forClass(UserEventMessageDTO.class);
        verify(eventPublisher).publishUserRoleChanged(eventCaptor.capture());
        JsonNode payload = eventCaptor.getValue().payload();

        assertEquals(UserEventType.USER_ROLE_CHANGED, eventCaptor.getValue().type());
        assertEquals(user.getEmail(), payload.path("userEmail").asString());
    }

    @Test
    void changeUserSameRoleThrowsError() {

        user.setRole(UserRole.ADMIN);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Exception exception = assertThrows(
                IllegalStateException.class,
                () -> adminUserService.changeUserRole(user.getId(), UserRole.ADMIN)
        );

        assertEquals("user already has this role", exception.getMessage());
    }

    // REACTIVATE USER TEST METHODS

    @Test
    void reactivateUserSuccessTest() {

        user.setActive(false);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response = adminUserService.reactivateUser(user.getId());

        assertEquals(1L, response.id());
        assertTrue(response.active());
    }

    @Test
    void reactivateUserAlreadyActiveThrowsError() {

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Exception exception = assertThrows(
                IllegalStateException.class,
                () -> adminUserService.reactivateUser(user.getId())
        );

        assertEquals("user is already active", exception.getMessage());
    }

    // DELETE PERMANENTLY USER TEST METHODS

    @Test
    void deletePermanentlyAdminThrowsError() {

        user.setRole(UserRole.ADMIN);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Exception exception = assertThrows(
                IllegalStateException.class,
                () -> adminUserService.deleteUserPermanently(user.getId())
        );

        assertEquals("only common users are allowed to be permanently deleted", exception.getMessage());
    }
}