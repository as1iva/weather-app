package org.as1iva.service;

import lombok.RequiredArgsConstructor;
import org.as1iva.dto.LocationRequestDto;
import org.as1iva.entity.Location;
import org.as1iva.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
