package org.as1iva.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SessionDto {

    private String id;

    private LocalDateTime expiresAt;
}
