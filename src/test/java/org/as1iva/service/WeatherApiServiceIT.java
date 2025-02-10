package org.as1iva.service;

import org.as1iva.config.TestConfig;
import org.as1iva.dto.response.LocationApiResponseDto;
import org.as1iva.dto.response.LocationResponseDto;
import org.as1iva.dto.response.WeatherApiResponseDto;
import org.as1iva.entity.User;
import org.as1iva.exception.api.ClientApiException;
import org.as1iva.exception.api.ServerApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Transactional
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
public class WeatherApiServiceIT {

    @Autowired
    private WeatherApiService weatherApiService;

    @Autowired
    private ExchangeFunction exchangeFunction;

    public static final User TEST_USER = User.builder()
            .id(1L)
            .login("test_login")
            .password("test_password")
            .build();

    @BeforeEach
    public void setup() {


        Mockito.reset(exchangeFunction);
    }

    @Test
    public void getLocations_shouldReturnExpectedLocation() {
        String jsonResponse = """
                [
                    {
                        "name": "London",
                        "lat": 51.5073219,
                        "lon": -0.1276474,
                        "country": "GB",
                        "state": "England"
                    }
                ]
                """;

        ClientResponse clientResponse = ClientResponse
                .create(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(jsonResponse)
                .build();

        given(exchangeFunction.exchange(any(ClientRequest.class)))
                .willReturn(Mono.just(clientResponse));

        List<LocationApiResponseDto> locations = weatherApiService.getLocations("London");

        assertAll(
                () -> assertThat(locations).isNotNull().hasSize(1),
                () -> assertThat(locations.get(0).getName()).isEqualTo("London"),
                () -> assertThat(locations.get(0).getCountry()).isEqualTo("GB"),
                () -> assertThat(locations.get(0).getState()).isEqualTo("England"),
                () -> assertThat(locations.get(0).getLatitude()).isEqualTo("51.5073219"),
                () -> assertThat(locations.get(0).getLongitude()).isEqualTo("-0.1276474")
        );
    }

    @Test
    public void getLocations_shouldThrowExceptionIf5xxError() {
        String jsonResponse = "[]";

        ClientResponse clientResponse = ClientResponse
                .create(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", "application/json")
                .body(jsonResponse)
                .build();

        given(exchangeFunction.exchange(any(ClientRequest.class)))
                .willReturn(Mono.just(clientResponse));

        assertThatThrownBy(() -> weatherApiService.getLocations("London"))
                .isInstanceOf(ServerApiException.class)
                .hasMessageContaining("Server unavailable. Please, try later");
    }

    @Test
    public void getWeatherByCoordinates_shouldReturnExpectedWeather() {
        String jsonResponse = """
                {
                    "coord": {
                        "lon": -0.1278,
                        "lat": 51.5099
                    },
                    "weather": [
                        {
                            "main": "Clouds",
                            "description": "overcast clouds"
                        }
                    ],
                    "main": {
                        "temp": 3.81,
                        "feels_like": 1.01,
                        "humidity": 90
                    },
                    "visibility": 10000,
                    "wind": {
                        "speed": 3.09
                    },
                    "clouds": {
                        "all": 100
                    },
                    "sys": {
                        "country": "GB",
                        "sunrise": 1739085932,
                        "sunset": 1739120642
                    },
                    "name": "London"
                }
                """;

        ClientResponse clientResponse = ClientResponse
                .create(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(jsonResponse)
                .build();

        given(exchangeFunction.exchange(any(ClientRequest.class)))
                .willReturn(Mono.just(clientResponse));

        LocationResponseDto location = LocationResponseDto.builder()
                .id(1L)
                .name("London")
                .state("England")
                .userId(TEST_USER)
                .latitude(BigDecimal.valueOf(51.5073))
                .longitude(BigDecimal.valueOf(-0.1276))
                .build();

        WeatherApiResponseDto weather = weatherApiService.getWeatherByCoordinates(location);

        weather.setName(location.getName());
        weather.setState(location.getState());

        assertAll(
                () -> assertThat(weather).isNotNull(),

                () -> assertThat(weather.getName()).isEqualTo("London"),
                () -> assertThat(weather.getState()).isEqualTo("England"),

                () -> assertThat(weather.getCoord().getLat()).isEqualTo("51.5099"),
                () -> assertThat(weather.getCoord().getLon()).isEqualTo("-0.1278"),

                () -> assertThat(weather.getWeather().get(0).getMain()).isEqualTo("Clouds"),
                () -> assertThat(weather.getWeather().get(0).getDescription()).isEqualTo("overcast clouds"),

                () -> assertThat(weather.getMain().getTemp()).isEqualTo(3),
                () -> assertThat(weather.getMain().getFeelsLike()).isEqualTo(1),
                () -> assertThat(weather.getMain().getHumidity()).isEqualTo(90),

                () -> assertThat(weather.getVisibility()).isEqualTo(10000),

                () -> assertThat(weather.getWind().getSpeed()).isEqualTo("3.09"),

                () -> assertThat(weather.getClouds().getAll()).isEqualTo(100),

                () -> assertThat(weather.getSys().getCountry()).isEqualTo("GB"),
                () -> assertThat(weather.getSys().getSunrise()).isEqualTo(1739085932),
                () -> assertThat(weather.getSys().getSunset()).isEqualTo(1739120642)
        );
    }

    @Test
    public void getWeatherByCoordinates_shouldThrowExceptionIf4xxError() {
        String jsonResponse = "[]";

        ClientResponse clientResponse = ClientResponse
                .create(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json")
                .body(jsonResponse)
                .build();

        given(exchangeFunction.exchange(any(ClientRequest.class)))
                .willReturn(Mono.just(clientResponse));

        LocationResponseDto location = LocationResponseDto.builder()
                .id(1L)
                .name("London")
                .state("England")
                .userId(TEST_USER)
                .latitude(BigDecimal.valueOf(51.5073))
                .longitude(BigDecimal.valueOf(-0.1276))
                .build();

        assertThatThrownBy(() -> weatherApiService.getWeatherByCoordinates(location))
                .isInstanceOf(ClientApiException.class)
                .hasMessageContaining("Client error. Please, try again");
    }
}
