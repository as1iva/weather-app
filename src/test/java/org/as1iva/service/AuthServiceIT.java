package org.as1iva.service;

import org.as1iva.config.TestConfig;
import org.as1iva.dto.SessionDto;
import org.as1iva.entity.Session;
import org.as1iva.entity.User;
import org.as1iva.exception.ExpiredSessionException;
import org.as1iva.exception.UserAuthenticationFailedException;
import org.as1iva.repository.SessionRepository;
import org.as1iva.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
public class AuthServiceIT {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    public static final String TEST_LOGIN = "test_login";

    public static final String TEST_PASSWORD = "test_password";

    public static final String WRONG_TEST_PASSWORD = "wrong_test_password";

    @Test
    void signUpUser_ShouldSaveUserAndCreateSession() {

        SessionDto sessionDto = authService.signUpUser(TEST_LOGIN, TEST_PASSWORD);
        assertThat(sessionDto).isNotNull();
        assertThat(sessionDto.getId()).isNotEmpty();

        Optional<User> userOptional = userRepository.findByLogin(TEST_LOGIN);
        assertThat(userOptional).isPresent();

        User user = userOptional.get();
        assertThat(user.getPassword()).isNotEqualTo(TEST_PASSWORD);

        Optional<Session> sessionOptional = sessionRepository.findById(sessionDto.getId());
        assertThat(sessionOptional).isPresent();

        Session session = sessionOptional.get();
        assertThat(session.getUserId().getId()).isEqualTo(user.getId());
    }

    @Test
    void signInUser_ShouldReturnSessionIfCorrectPassword() {

        authService.signUpUser(TEST_LOGIN, TEST_PASSWORD);

        SessionDto sessionDto = authService.signInUser(TEST_LOGIN, TEST_PASSWORD);
        assertThat(sessionDto).isNotNull();

        Optional<Session> sessionOptional = sessionRepository.findById(sessionDto.getId());
        assertThat(sessionOptional).isPresent();

        Session session = sessionOptional.get();
        assertThat(session.getUserId().getLogin()).isEqualTo(TEST_LOGIN);
    }

    @Test
    void signInUser_ShouldThrowExceptionIfWrongPassword() {

        authService.signUpUser(TEST_LOGIN, TEST_PASSWORD);

        assertThatThrownBy(() -> authService.signInUser(TEST_LOGIN, WRONG_TEST_PASSWORD))
                .isInstanceOf(UserAuthenticationFailedException.class)
                .hasMessageContaining("Incorrect username or password");
    }

    @Test
    void getUserBySession_whenSessionExpired_thenThrowExpiredSessionException() {

        SessionDto sessionDto = authService.signUpUser(TEST_LOGIN, TEST_PASSWORD);
        String sessionId = sessionDto.getId();

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(AssertionError::new);

        session.setExpiresAt(LocalDateTime.now().minusDays(1));
        sessionRepository.save(session);

        assertThatThrownBy(() -> authService.getUserBySession(sessionId))
                .isInstanceOf(ExpiredSessionException.class)
                .hasMessageContaining("Session expired");

        assertThat(sessionRepository.findById(sessionId)).isEmpty();
    }
}

