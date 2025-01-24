package org.as1iva.service;

import lombok.RequiredArgsConstructor;
import org.as1iva.dto.LocationRequestDto;
import org.as1iva.dto.LocationResponseDto;
import org.as1iva.entity.Location;
import org.as1iva.entity.User;
import org.as1iva.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public void add(LocationRequestDto locationRequestDto) {
        Location location = Location.builder()
                .name(locationRequestDto.getName())
                .userId(locationRequestDto.getUserId())
                .latitude(locationRequestDto.getLatitude())
                .longitude(locationRequestDto.getLongitude())
                .build();

        locationRepository.save(location);
    }

    public List<LocationResponseDto> getAllByUserId(User userId) {
        List<LocationResponseDto> locationResponseDtos = new ArrayList<>();

        List<Location> locations = locationRepository.findAllByUserId(userId);

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
