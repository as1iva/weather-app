package org.as1iva.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.as1iva.entity.Session;
import org.as1iva.entity.User;
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

    public Session createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime time = LocalDateTime.now();

        Session session = Session.builder()
                .id(sessionId)
                .userId(user)
                .expiresAt(time.plusDays(5))
                .build();

        return sessionRepository.save(session);
    }

    public Optional<Session> getSession(String id) {
        return sessionRepository.findById(id);
    }

    public void deleteSession(String sessionId) {
        if(sessionId != null) {
            sessionRepository.delete(sessionId);
        }
    }

    public void createCookie(Session session, HttpServletResponse resp) {
        Cookie cookie = new Cookie("sessionId", session.getId());
        cookie.setAttribute("expires_at", session.getExpiresAt().toString());
        resp.addCookie(cookie);
    }

    public Cookie getCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("sessionId")) {
                    return cookie;
                }
            }
        }

        return null;
    }

    public void deleteCookie(HttpServletResponse resp, String attribute){
        Cookie cookie = new Cookie(attribute, null);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }
}
