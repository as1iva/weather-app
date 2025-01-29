package org.as1iva.service;

import lombok.RequiredArgsConstructor;
import org.as1iva.dto.UserDto;
import org.as1iva.dto.request.LocationRequestDto;
import org.as1iva.dto.response.LocationResponseDto;
import org.as1iva.entity.Location;
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
        User user = authService.getSession(sessionId).get().getUserId();

        Location location = Location.builder()
                .name(locationRequestDto.getName())
                .userId(user)
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
            locationResponseDtos.add(LocationResponseDto.builder()
                    .id(location.getId())
                    .name(location.getName())
                    .userId(location.getUserId())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build());
        }

        return locationResponseDtos;
    }
}
