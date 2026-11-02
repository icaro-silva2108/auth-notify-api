package serviceTests;

import com.icaro.auth_notify.common.exceptions.EmailAlreadyExistsException;
import com.icaro.auth_notify.common.exceptions.InvalidAgeException;
import com.icaro.auth_notify.common.exceptions.ResourceNotFoundException;
import com.icaro.auth_notify.messaging.dto.UserEventMessageDTO;
import com.icaro.auth_notify.messaging.enums.UserEventType;
import com.icaro.auth_notify.user.model.User;
import com.icaro.auth_notify.user.model.dto.UserRequestDTO;
import com.icaro.auth_notify.user.model.dto.UserResponseDTO;
import com.icaro.auth_notify.user.model.dto.UserUpdateDTO;
import com.icaro.auth_notify.user.model.enums.AuthProvider;
import com.icaro.auth_notify.user.repository.UserRepository;
import com.icaro.auth_notify.messaging.publisher.UserEventPublisher;

import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import com.icaro.auth_notify.user.service.UserService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    // MOCKS

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserEventPublisher eventPublisher;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private UserService userService;

    // SETUP

    private UserRequestDTO createUserRequest;
    private UserUpdateDTO updateUserRequest;
    private User user;

    @BeforeEach
    public void setup() {

        createUserRequest = new UserRequestDTO(
            "test",
            "test@test.com",
            "test1234",
            LocalDate.of(2000, 1, 1)
        );

        updateUserRequest = new UserUpdateDTO(
                "newUser",
                "new@email.com",
                "newPasswordHash",
                LocalDate.of(2000, 2, 2)
        );

        user = User.builder()
            .name("test")
            .email("test@test.com")
            .passwordHash("passwordHash")
            .birthDate(LocalDate.of(2000, 1, 1))
            .build();

        ReflectionTestUtils.setField(user, "id", 1L);
    }

    // CREATE USER TEST METHODS

    @Test
    void createUserSuccessTest() {

        when(userRepository.existsByEmail(createUserRequest.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(createUserRequest.password()))
                .thenReturn("passwordHash");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    ReflectionTestUtils.setField(u, "id", 1L);
                    return u;
                });

        UserResponseDTO response = userService.createUser(createUserRequest);

        assertEquals(1L, response.id());
        assertEquals("test", response.name());
        assertEquals("test@test.com", response.email());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertEquals("passwordHash", userCaptor.getValue().getPasswordHash());
        assertTrue(userCaptor.getValue().getProviders().contains(AuthProvider.LOCAL));

        ArgumentCaptor<UserEventMessageDTO> eventCaptor = ArgumentCaptor.forClass(UserEventMessageDTO.class);
        verify(eventPublisher).publishUserCreated(eventCaptor.capture());
        JsonNode payload = eventCaptor.getValue().payload();

        assertEquals(UserEventType.USER_CREATED, eventCaptor.getValue().type());
        assertEquals(response.name(), payload.path("name").asString());
        assertEquals(response.email(), payload.path("userEmail").asString());
    }

    @Test
    void createUserEmailConflictThrowsError() {

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.createUser(createUserRequest)
        );
    }

    @Test
    void createUserInvalidAgeThrowsError() {

        UserRequestDTO invalidAgeRequest = new UserRequestDTO(
                "test",
                "test@test.com",
                "test1234",
                LocalDate.now().minusYears(13)
        );

        assertThrows(
                InvalidAgeException.class,
                () -> userService.createUser(invalidAgeRequest)
        );
    }

    // UPDATE USER TEST METHODS

    @Test
    void updateUserSuccessTest() {

        when(userRepository.existsByEmail(updateUserRequest.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(updateUserRequest.password()))
                .thenReturn("newPasswordHash");

        when(userRepository.save(user))
                .thenReturn(user);

        UserResponseDTO response = userService.updateUser(updateUserRequest, user);

        assertEquals(response.id(), user.getId());
        assertEquals(response.name(), user.getName());
        assertEquals(response.email(), user.getEmail());
        assertEquals(response.birthDate(), user.getBirthDate());
        assertEquals("newPasswordHash", user.getPasswordHash());

        ArgumentCaptor<UserEventMessageDTO> eventCaptor = ArgumentCaptor.forClass(UserEventMessageDTO.class);
        verify(eventPublisher).publishUserUpdated(eventCaptor.capture());
        JsonNode payload = eventCaptor.getValue().payload();

        assertEquals(UserEventType.USER_UPDATED, eventCaptor.getValue().type());
        assertEquals(response.name(), payload.path("name").asString());
        assertEquals(response.email(), payload.path("userEmail").asString());
    }

    @Test
    void updateUserEmailConflictThrowsError() {

        when(userRepository.existsByEmail(updateUserRequest.email()))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.updateUser(updateUserRequest, user)
        );
    }

    @Test
    void updateUserInvalidAgeThrowsError() {

        UserUpdateDTO invalidAgeRequest = new UserUpdateDTO(
                "newUser",
                "new@email.com",
                "newPasswordHash",
                LocalDate.now().minusYears(13)
        );

        assertThrows(
                InvalidAgeException.class,
                () -> userService.updateUser(invalidAgeRequest, user)
        );
    }

    // DELETE USER TEST METHODS

    @Test
    void deleteUserSuccessTest() {

        when(userRepository.save(user))
                .thenReturn(user);

        userService.deleteUser(user);
        assertFalse(user.isActive());

        ArgumentCaptor<UserEventMessageDTO> eventCaptor = ArgumentCaptor.forClass(UserEventMessageDTO.class);
        verify(eventPublisher).publishUserDeactivated(eventCaptor.capture());
        JsonNode payload = eventCaptor.getValue().payload();

        assertEquals(UserEventType.USER_DEACTIVATED, eventCaptor.getValue().type());
        assertEquals(user.getName(), payload.path("name").asString());
        assertEquals(user.getEmail(), payload.path("userEmail").asString());
    }

    @Test
    void deleteUserAlreadyDeactivatedThrowsError() {

        user.setActive(false);

        assertThrows(
                IllegalStateException.class,
                () -> userService.deleteUser(user)
        );
    }

    // FIND USER TEST METHODS

    @Test
    void notFoundUserThrowsError() {

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findUser(user)
        );
    }
}