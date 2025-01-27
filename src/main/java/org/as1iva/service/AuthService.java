package org.as1iva.service;

import lombok.RequiredArgsConstructor;
import org.as1iva.dto.SessionDto;
import org.as1iva.entity.Session;
import org.as1iva.entity.User;
import org.as1iva.exception.UserAuthenticationFailedException;
import org.as1iva.repository.SessionRepository;
import org.as1iva.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final SessionRepository sessionRepository;

    private final UserRepository userRepository;

    public boolean isPasswordCorrect(String password, User user) {
        return BCrypt.checkpw(password, user.getPassword());
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public SessionDto signInUser(String login, String password) {
        Optional<User> user = userRepository.findByLogin(login);

        if (!user.isPresent() || !isPasswordCorrect(password, user.get())) {
            throw new UserAuthenticationFailedException("Incorrect username or password");
        }

        return createSession(user.get());
    }

    public User createUser(String login, String password) {
        User user = User.builder()
                .login(login)
                .password(hashPassword(password))
                .build();

        return userRepository.save(user);
    }

    public Optional<User> findUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public SessionDto createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime time = LocalDateTime.now();

        Session session = Session.builder()
                .id(sessionId)
                .userId(user)
                .expiresAt(time.plusDays(5))
                .build();

        sessionRepository.save(session);

        return SessionDto.builder()
                .id(session.getId())
                .expiresAt(session.getExpiresAt())
                .build();
    }

    public Optional<Session> getSession(String id) {
        return sessionRepository.findById(id);
    }

    public void deleteSession(String sessionId) {
        if(sessionId != null) {
            sessionRepository.delete(sessionId);
        }
    }
}
