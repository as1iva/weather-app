package org.as1iva.controller;

import lombok.RequiredArgsConstructor;
import org.as1iva.dto.UserDto;
import org.as1iva.dto.request.LocationRequestDto;
import org.as1iva.dto.response.LocationApiResponseDto;
import org.as1iva.service.AuthService;
import org.as1iva.service.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private static final String SEARCH = "search-results";

    private static final String REDIRECT_TO_INDEX = "redirect:/";

    private final LocationService locationService;

    private final AuthService authService;

    @GetMapping("/search")
    public String searchLocation(@RequestParam(name = "name") String name,
                                 @CookieValue(name = "sessionId", required = false) String sessionId,
                                 Model model) {

        UserDto user = authService.getUserBySession(sessionId);

        List<LocationApiResponseDto> locations = locationService.getAvailable(user, name);

        model.addAttribute("name", name);
        model.addAttribute("username", user.getLogin());
        model.addAttribute(locations);

        return SEARCH;
    }

    @PostMapping("/search")
    public String addLocation(@RequestParam(name = "name") String name,
                              @RequestParam(name = "lat") BigDecimal lat,
                              @RequestParam(name = "lon") BigDecimal lon,
                              @CookieValue(name = "sessionId", required = false) String sessionId) {

        LocationRequestDto locationRequestDto = LocationRequestDto.builder()
                .name(name)
                .latitude(lat)
                .longitude(lon)
                .build();

        locationService.add(locationRequestDto, sessionId);

        return REDIRECT_TO_INDEX;
    }
}
