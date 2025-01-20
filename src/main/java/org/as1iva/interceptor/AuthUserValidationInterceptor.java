package org.as1iva.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.as1iva.service.AuthService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

@Component
@RequiredArgsConstructor
public class AuthUserValidationInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest req,
                             @NonNull HttpServletResponse resp,
                             @NonNull Object handler) throws Exception {

        Cookie cookie = WebUtils.getCookie(req, "sessionId");

        if (isSessionValid(cookie)) {
            resp.sendRedirect("/");

            return false;
        }

        return true;
    }

    private boolean isSessionValid(Cookie cookie) {
        return (cookie != null && cookie.getValue() != null && authService.getSession(cookie.getValue()).isPresent());
    }
}
