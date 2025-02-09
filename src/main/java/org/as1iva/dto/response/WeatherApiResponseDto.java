package org.as1iva.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiResponseDto {

    private String name;

    private String state;

    private CoordinatesDto coord;

    private List<WeatherDto> weather;

    private MainDto main;

    private Integer visibility;

    private WindDto wind;

    private CloudsDto clouds;

    private SysDto sys;

    @Getter
    @Setter
    public static class CoordinatesDto {

        private BigDecimal lat;

        private BigDecimal lon;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherDto {

        private Integer id;

        private String main;

        private String description;

        private String icon;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainDto {

        private Integer temp;

        @JsonProperty("feels_like")
        private Integer feelsLike;

        private Integer humidity;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WindDto {

        private BigDecimal speed;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CloudsDto {

        private Integer all;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SysDto {

        private String country;

        private Long sunrise;

        private Long sunset;
    }
}
