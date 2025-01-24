package org.as1iva.controller;

import lombok.RequiredArgsConstructor;
import org.as1iva.entity.Session;
import org.as1iva.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private static final String INDEX = "index";

    private final AuthService authService;

    @GetMapping("/")
    public String home(@CookieValue(name = "sessionId", required = false) String sessionId,
                       Model model) {

        Session session = authService.getSession(sessionId).get();

        model.addAttribute("username", session.getUserId().getLogin());

        return INDEX;
    }
}
