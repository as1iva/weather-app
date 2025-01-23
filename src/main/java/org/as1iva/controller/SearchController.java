package org.as1iva.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.as1iva.dto.LocationRequestDto;
import org.as1iva.dto.LocationApiResponseDto;
import org.as1iva.entity.Session;
import org.as1iva.entity.User;
import org.as1iva.service.AuthService;
import org.as1iva.service.LocationService;
import org.as1iva.service.WeatherApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private static final String SEARCH = "search-results";

    private static final String REDIRECT_TO_INDEX = "redirect:/";

    private final LocationService locationService;

    private final WeatherApiService weatherApiService;

    private final AuthService authService;

    @GetMapping("/search")
    public String searchLocation(@RequestParam(name = "name") String name,
                                 Model model,
                                 HttpServletRequest req) throws JsonProcessingException {

        Cookie cookie = WebUtils.getCookie(req, "sessionId");
        Session session = authService.getSession(cookie.getValue()).get();

        List<LocationApiResponseDto> locations = weatherApiService.getLocations(name);

        model.addAttribute("name", name);
        model.addAttribute("username", session.getUserId().getLogin());
        model.addAttribute(locations);

        return SEARCH;
    }

    @PostMapping("/search")
    public String addLocation(@RequestParam(name = "name") String name,
                              @RequestParam(name = "lat") BigDecimal lat,
                              @RequestParam(name = "lon") BigDecimal lon,
                              @CookieValue(name = "sessionId", required = false) String sessionId) {

        User user = authService.getSession(sessionId).get().getUserId();

        LocationRequestDto locationRequestDto = LocationRequestDto.builder()
                .name(name)
                .userId(user)
                .latitude(lat)
                .longitude(lon)
                .build();

        locationService.add(locationRequestDto);

        return REDIRECT_TO_INDEX;
    }
}
