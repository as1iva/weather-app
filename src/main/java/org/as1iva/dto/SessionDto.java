package org.as1iva.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SessionDto {

    private final String id;

    private final LocalDateTime expiresAt;
}
