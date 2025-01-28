package org.as1iva.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.as1iva.dto.SessionDto;
import org.as1iva.entity.User;
import org.as1iva.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private static final String REDIRECT_TO_INDEX = "redirect:/";
    private static final String REDIRECT_TO_LOGIN = "redirect:/login";

    private static final String SIGN_IN = "sign-in";
    private static final String SIGN_UP = "sign-up";

    private final AuthService authService;

    @GetMapping("/login")
    public String signIn() {
        return SIGN_IN;
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam(name = "username") String login,
                            @RequestParam(name = "password") String password,
                            HttpServletResponse resp) {

        SessionDto sessionDto = authService.signInUser(login, password);

        createCookie(sessionDto, resp);

        return REDIRECT_TO_INDEX;
    }

    @GetMapping("/registration")
    public String signUp() {
        return SIGN_UP;
    }

    @PostMapping("/registration")
    public String registerUser(@RequestParam(name = "username") String login,
                               @RequestParam(name = "password") String password,
                               @RequestParam(name = "repeat-password") String repeatPassword,
                               HttpServletResponse resp,
                               Model model) {

        model.addAttribute("username", login);

        if (!password.equals(repeatPassword)) {
            String error = "Password doesn't match";
            model.addAttribute("messageError", error);
        }

        if (password.length() < 3) {
            String error = "Password should be longer than 3";
            model.addAttribute("passwordError", error);
        }

        if (authService.findUserByLogin(login).isPresent()) {
            String error = "This user already exists";
            model.addAttribute("usernameError", error);
        }

        if (model.containsAttribute("messageError")
                || model.containsAttribute("passwordError")
                || model.containsAttribute("usernameError")) {

            return SIGN_UP;
        }

        User user = authService.createUser(login, password);

        Session session = authService.createSession(user);
        createCookie(session, resp);

        return REDIRECT_TO_INDEX;
    }

    @PostMapping("/logout")
    public String logoutUser(@CookieValue(name = "sessionId", required = false) String sessionId,
                             HttpServletResponse resp) {

        authService.deleteSession(sessionId);
        deleteCookie(resp);

        return REDIRECT_TO_LOGIN;
    }

    private void createCookie(SessionDto sessionDto, HttpServletResponse resp) {
        Cookie cookie = new Cookie("sessionId", sessionDto.getId());
        cookie.setAttribute("expires_at", sessionDto.getExpiresAt().toString());
        resp.addCookie(cookie);
    }

    private void deleteCookie(HttpServletResponse resp) {
        Cookie cookie = new Cookie("sessionId", null);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }
}
