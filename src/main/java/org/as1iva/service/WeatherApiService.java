package org.as1iva.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.as1iva.dto.response.LocationApiResponseDto;
import org.as1iva.dto.response.LocationResponseDto;
import org.as1iva.dto.response.WeatherApiResponseDto;
import org.as1iva.exception.api.JsonParsingApiException;
import org.as1iva.exception.api.ClientApiException;
import org.as1iva.exception.api.ServerApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WeatherApiService {

    private final WebClient webClient;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String CLIENT_ERROR_MESSAGE = "Client error. Please, try again";

    public static final String SERVER_ERROR_MESSAGE = "Server unavailable. Please, try later";

    public static final String JSON_ERROR_MESSAGE = "Service unavailable";

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
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            Mono.error(new ClientApiException(CLIENT_ERROR_MESSAGE)))
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            Mono.error(new ServerApiException(SERVER_ERROR_MESSAGE)))
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(jsonResponse, new TypeReference<List<LocationApiResponseDto>>() {});
        } catch (JsonProcessingException e) {
            throw new JsonParsingApiException(JSON_ERROR_MESSAGE);
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
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            Mono.error(new ClientApiException(CLIENT_ERROR_MESSAGE)))
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            Mono.error(new ServerApiException(SERVER_ERROR_MESSAGE)))
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(jsonResponse, WeatherApiResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new JsonParsingApiException(JSON_ERROR_MESSAGE);
        }
    }
}
