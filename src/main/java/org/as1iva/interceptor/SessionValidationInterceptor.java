package org.as1iva.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.as1iva.entity.Session;
import org.as1iva.service.AuthService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SessionValidationInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest req,
                             @NonNull HttpServletResponse resp,
                             @NonNull Object handler) throws Exception {

        Cookie cookie = WebUtils.getCookie(req, "sessionId");

        if (isSessionOld(cookie)) {
            authService.deleteSession(cookie.getValue());

            resp.sendRedirect("/login");

            return false;
        }

        return true;
    }

    private boolean isSessionOld(Cookie cookie) {
        Optional<Session> session = authService.getSession(cookie.getValue());

        return session.get().getExpiresAt().isBefore(LocalDateTime.now());
    }
}
