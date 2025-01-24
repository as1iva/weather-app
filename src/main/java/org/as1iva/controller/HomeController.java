package org.as1iva.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.as1iva.dto.LocationResponseDto;
import org.as1iva.dto.WeatherApiResponseDto;
import org.as1iva.entity.Session;
import org.as1iva.service.AuthService;
import org.as1iva.service.LocationService;
import org.as1iva.service.WeatherApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private static final String INDEX = "index";

    private final AuthService authService;

    private final LocationService locationService;

    private final WeatherApiService weatherApiService;

    @GetMapping("/")
    public String home(@CookieValue(name = "sessionId", required = false) String sessionId,
                       Model model) throws JsonProcessingException {

        Session session = authService.getSession(sessionId).get();
        model.addAttribute("username", session.getUserId().getLogin());

        List<LocationResponseDto> locations = locationService.getAllByUserId(session.getUserId());

        List<WeatherApiResponseDto> weatherApiResponseDtos = new ArrayList<>();

        for (LocationResponseDto location : locations) {
            WeatherApiResponseDto weather = weatherApiService.getWeatherByCoordinates(location);

            weatherApiResponseDtos.add(weather);
        }

        model.addAttribute(weatherApiResponseDtos);

        return INDEX;
    }
}
