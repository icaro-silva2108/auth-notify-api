package serviceTests;

import com.icaro.auth_notify.messaging.dto.UserEventMessageDTO;
import com.icaro.auth_notify.messaging.enums.UserEventType;
import com.icaro.auth_notify.user.model.User;
import com.icaro.auth_notify.user.model.dto.UserRequestDTO;
import com.icaro.auth_notify.user.model.dto.UserResponseDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

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

    private UserRequestDTO request;
    private User user;

    @BeforeEach
    public void setup() {

        request = new UserRequestDTO(
            "test",
            "test@test.com",
            "test1234",
            LocalDate.of(2000, 1, 1)
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
    public void createUserSuccessTest() {

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("passwordHash");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    ReflectionTestUtils.setField(u, "id", 1L);
                    return u;
                });

        UserResponseDTO response = userService.createUser(request);

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
        assertEquals("test", payload.path("name").asString());
        assertEquals("test@test.com", payload.path("userEmail").asString());
    }
}