package org.as1iva.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.as1iva.dto.LocationResponseDto;
import org.as1iva.entity.Session;
import org.as1iva.service.AuthService;
import org.as1iva.service.WeatherApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private static final String SEARCH = "search-results";

    private final WeatherApiService weatherApiService;

    private final AuthService authService;

    @GetMapping("/search")
    public String search(@RequestParam(name = "name") String name,
                         Model model,
                         HttpServletRequest req) throws JsonProcessingException {

        Cookie cookie = WebUtils.getCookie(req, "sessionId");
        Session session = authService.getSession(cookie.getValue()).get();

        List<LocationResponseDto> locations = weatherApiService.getLocations(name);

        model.addAttribute("username", session.getUserId().getLogin());
        model.addAttribute(locations);

        return SEARCH;
    }
}
