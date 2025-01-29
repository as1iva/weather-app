package org.as1iva.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class LocationRequestDto {

    private final String name;

    private final BigDecimal latitude;

    private final BigDecimal longitude;
}
