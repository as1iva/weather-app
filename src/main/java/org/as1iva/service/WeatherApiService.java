package org.as1iva.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.as1iva.dto.response.LocationApiResponseDto;
import org.as1iva.dto.response.LocationResponseDto;
import org.as1iva.dto.response.WeatherApiResponseDto;
import org.as1iva.exception.ApiRequestFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WeatherApiService {

    private final WebClient webClient;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.key}")
    private String apiKey;

    public List<LocationApiResponseDto> getLocations(String city) {
        try {
            String jsonResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/geo/1.0/direct")
                            .queryParam("q", city)
                            .queryParam("limit", 5)
                            .queryParam("appId", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(jsonResponse, new TypeReference<List<LocationApiResponseDto>>() {});
        } catch (JsonProcessingException e) {
            throw new ApiRequestFailedException("Service unavailable");
        }
    }

    public WeatherApiResponseDto getWeatherByCoordinates(LocationResponseDto location) {
        try {
            String jsonResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/data/2.5/weather")
                            .queryParam("lat", location.getLatitude())
                            .queryParam("lon", location.getLongitude())
                            .queryParam("units", "metric")
                            .queryParam("appId", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(jsonResponse, WeatherApiResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new ApiRequestFailedException("Service unavailable");
        }
    }
}
