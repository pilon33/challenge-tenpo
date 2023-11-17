package com.project.challenge.infrastructure.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
public class CachedPercentage {

    private double percentage;
    private OffsetDateTime createdTime;

    public CachedPercentage(double percentage) {
        this.percentage = percentage;
        this.createdTime = OffsetDateTime.now(ZoneOffset.UTC);
    }

}
