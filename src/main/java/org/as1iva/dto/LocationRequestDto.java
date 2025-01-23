package org.as1iva.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.as1iva.entity.User;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class LocationRequestDto {

    private final String name;

    private final User userId;

    private final BigDecimal latitude;

    private final BigDecimal longitude;
}
