package org.as1iva.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.as1iva.dto.SessionDto;
import org.as1iva.dto.UserRegistrationRequestDto;
import org.as1iva.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public String signUp(@ModelAttribute("userInfo") UserRegistrationRequestDto userRegistrationRequestDto) {
        return SIGN_UP;
    }

    @PostMapping("/registration")
    public String registerUser(@ModelAttribute("userInfo") @Valid UserRegistrationRequestDto userRegistrationRequestDto,
                               BindingResult bindingResult,
                               HttpServletResponse resp) {

        String login = userRegistrationRequestDto.getLogin();
        String password = userRegistrationRequestDto.getPassword();
        String repeatPassword = userRegistrationRequestDto.getRepeatPassword();

        if (!password.equals(repeatPassword)) {
            bindingResult.rejectValue("repeatPassword", "error.repeatPassword", "Passwords doesn't match");
        }

        if (authService.findUserByLogin(login).isPresent()) {
            bindingResult.rejectValue("login", "error.login", "This user already exists");
        }

        if (bindingResult.hasErrors()) {
            return SIGN_UP;
        }

        SessionDto sessionDto = authService.signUpUser(login, password);

        createCookie(sessionDto, resp);

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
