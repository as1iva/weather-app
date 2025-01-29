package org.as1iva.service;

import lombok.RequiredArgsConstructor;
import org.as1iva.dto.UserDto;
import org.as1iva.dto.request.LocationRequestDto;
import org.as1iva.dto.response.LocationResponseDto;
import org.as1iva.entity.Location;
import org.as1iva.entity.Session;
import org.as1iva.entity.User;
import org.as1iva.exception.DataNotFoundException;
import org.as1iva.repository.LocationRepository;
import org.as1iva.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {

    private final AuthService authService;

    private final LocationRepository locationRepository;

    private final UserRepository userRepository;

    public void add(LocationRequestDto locationRequestDto, String sessionId) {

        Session session = authService.getSession(sessionId)
                .orElseThrow(() -> new DataNotFoundException("Session was not found"));

        Location location = Location.builder()
                .name(locationRequestDto.getName())
                .userId(session.getUserId())
                .latitude(locationRequestDto.getLatitude())
                .longitude(locationRequestDto.getLongitude())
                .build();

        locationRepository.save(location);
    }

    public List<LocationResponseDto> getAllByUserId(UserDto userDto) {

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
}
