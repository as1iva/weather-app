package org.as1iva.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.as1iva.entity.Session;
import org.as1iva.entity.User;
import org.as1iva.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private static final String REDIRECT_TO_INDEX = "redirect:/";

    private static final String REDIRECT_TO_LOGIN = "redirect:/login";

    private final AuthService authService;

    @GetMapping("/login")
    public String signIn() {
        return "sign-in";
    }

    @Transactional
    @PostMapping("/login")
    public String loginUser(@RequestParam(name = "username") String login,
                            @RequestParam(name = "password") String password,
                            Model model,
                            HttpServletResponse resp) {

        model.addAttribute("username", login);

        Optional<User> user = authService.findUserByLogin(login);

        if (!user.isPresent() || !authService.isPasswordCorrect(password, user.get())) {
            String error = "Incorrect username or password";

            model.addAttribute("messageError", error);

            return "sign-in-with-errors";
        }

        Session session = authService.createSession(user.get());
        authService.createCookie(session, resp);

        return REDIRECT_TO_INDEX;
    }

    @GetMapping("/registration")
    public String signUp() {
        return "sign-up";
    }

    @Transactional
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

            return "sign-up-with-errors";
        }

        User user = authService.createUser(login, password);

        Session session = authService.createSession(user);
        authService.createCookie(session, resp);

        return REDIRECT_TO_INDEX;
    }

    @Transactional
    @PostMapping("/logout")
    public String logoutUser(@CookieValue(name = "sessionId", required = false) String sessionId,
                             HttpServletResponse resp) {

        authService.deleteSession(sessionId);
        authService.deleteCookie(resp, "sessionId");

        return REDIRECT_TO_LOGIN;
    }
}
