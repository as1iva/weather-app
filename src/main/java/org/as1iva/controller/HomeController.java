package org.as1iva.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.as1iva.entity.Session;
import org.as1iva.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final AuthService authService;

    @GetMapping("/")
    public String home(Model model, HttpServletRequest req) {

        Cookie cookie = WebUtils.getCookie(req, "sessionId");
        Session session = authService.getSession(cookie.getValue()).get();

        model.addAttribute("username", session.getUserId().getLogin());

        return "index";
    }

    @PostMapping("/")
    public String search(String name, RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("name", name);

        return "redirect:/search";
    }
}
