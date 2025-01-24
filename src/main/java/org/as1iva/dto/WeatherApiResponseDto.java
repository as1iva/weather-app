package org.as1iva.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiResponseDto {

    private String name;

    private List<WeatherDto> weather;

    private MainDto main;

    private Integer visibility;

    private WindDto wind;

    private CloudsDto clouds;

    private SysDto sys;

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

        private Integer feels_like;

        private Integer humidity;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WindDto {

        private Double speed;
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
    public static  class SysDto {

        private String country;

        private Integer sunrise;

        private Integer sunset;
    }
}
