package org.as1iva.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.as1iva.dto.UserDto;
import org.as1iva.dto.response.LocationResponseDto;
import org.as1iva.dto.response.WeatherApiResponseDto;
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

        UserDto user = authService.getUserBySession(sessionId);

        List<LocationResponseDto> locations = locationService.getAllByUserId(user);

        List<WeatherApiResponseDto> weatherApiResponses = new ArrayList<>();

        for (LocationResponseDto location : locations) {
            WeatherApiResponseDto weather = weatherApiService.getWeatherByCoordinates(location);

            weatherApiResponseDtos.add(weather);
        }

        model.addAttribute(weatherApiResponseDtos);
        model.addAttribute("username", user.getLogin());

        return INDEX;
    }
}
