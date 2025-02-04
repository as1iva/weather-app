package org.as1iva.service;

import lombok.RequiredArgsConstructor;
import org.as1iva.dto.UserDto;
import org.as1iva.dto.request.LocationRequestDto;
import org.as1iva.dto.response.LocationApiResponseDto;
import org.as1iva.dto.response.LocationResponseDto;
import org.as1iva.dto.response.WeatherApiResponseDto;
import org.as1iva.entity.Location;
import org.as1iva.entity.Session;
import org.as1iva.entity.User;
import org.as1iva.exception.DataNotFoundException;
import org.as1iva.exception.DuplicateLocationException;
import org.as1iva.repository.LocationRepository;
import org.as1iva.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {

    private final AuthService authService;

    private final WeatherApiService weatherApiService;

    private final LocationRepository locationRepository;

    private final UserRepository userRepository;

    public void add(LocationRequestDto locationRequestDto, String sessionId) {

        Session session = authService.getSession(sessionId)
                .orElseThrow(() -> new DataNotFoundException("Session was not found"));

        User user = session.getUserId();
        BigDecimal lat = locationRequestDto.getLatitude().setScale(4, RoundingMode.HALF_UP);
        BigDecimal lon = locationRequestDto.getLongitude().setScale(4, RoundingMode.HALF_UP);

        Optional<Location> locationOptional = locationRepository.findByCoordinates(user, lat, lon);

        if (locationOptional.isPresent()) {
            throw new DuplicateLocationException("Location already exists");
        }

        Location location = Location.builder()
                .name(locationRequestDto.getName())
                .userId(user)
                .latitude(lat)
                .longitude(lon)
                .build();

        locationRepository.save(location);
    }

    public List<LocationApiResponseDto> getAvailable(UserDto userDto, String name) {

        User user = userRepository.findByLogin(userDto.getLogin())
                .orElseThrow(() -> new DataNotFoundException("User was not found"));

        List<LocationApiResponseDto> apiLocations = weatherApiService.getLocations(name);

        List<Location> locations = locationRepository.findAllByUserId(user);

        List<LocationApiResponseDto> locationsToDelete = new ArrayList<>();

        for (Location location : locations) {
            for (LocationApiResponseDto apiLocation : apiLocations) {

                BigDecimal roundedLat = apiLocation.getLatitude().setScale(4, RoundingMode.HALF_UP);
                BigDecimal roundedLon = apiLocation.getLongitude().setScale(4, RoundingMode.HALF_UP);

                if (location.getLatitude().equals(roundedLat) && location.getLongitude().equals(roundedLon)) {
                    locationsToDelete.add(apiLocation);
                    break;
                }
            }
        }

        apiLocations.removeAll(locationsToDelete);

        return apiLocations;
    }

    public List<WeatherApiResponseDto> getWeather(UserDto user) {

        List<LocationResponseDto> locations = getAllByUserId(user);

        List<WeatherApiResponseDto> weatherApiResponses = new ArrayList<>();

        for (LocationResponseDto location : locations) {
            WeatherApiResponseDto weather = weatherApiService.getWeatherByCoordinates(location);

            String state = getStateByCoordinates(location).orElse("N/A");

            weather.setState(state);

            weatherApiResponses.add(weather);
        }

        return weatherApiResponses;
    }

    private Optional<String> getStateByCoordinates(LocationResponseDto location) {

        List<LocationApiResponseDto> apiLocations = weatherApiService.getLocations(location.getName());

        for (LocationApiResponseDto apiLocation: apiLocations) {

            BigDecimal roundedLat = apiLocation.getLatitude().setScale(4, RoundingMode.HALF_UP);
            BigDecimal roundedLon = apiLocation.getLongitude().setScale(4, RoundingMode.HALF_UP);

            if (roundedLat.equals(location.getLatitude()) && roundedLon.equals(location.getLongitude())) {
                return Optional.of(apiLocation.getState());
            }
        }

        return Optional.empty();
    }

    private List<LocationResponseDto> getAllByUserId(UserDto userDto) {

        User user = userRepository.findByLogin(userDto.getLogin())
                .orElseThrow(() -> new DataNotFoundException("User was not found"));

        List<LocationResponseDto> locationResponses = new ArrayList<>();

        List<Location> locations = locationRepository.findAllByUserId(user);

        for (Location location : locations) {
            locationResponses.add(LocationResponseDto.builder()
                    .id(location.getId())
                    .name(location.getName())
                    .userId(location.getUserId())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build());
        }

        return locationResponses;
    }

    public void delete(UserDto userDto, BigDecimal lat, BigDecimal lon) {

        User user = userRepository.findByLogin(userDto.getLogin())
                .orElseThrow(() -> new DataNotFoundException("User was not found"));

        locationRepository.deleteByCoordinates(user, lat, lon);
    }
}
