package com.project.challenge.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PercentageResponseDTO {

    private int number1;
    private int number2;
    private double percentage;
    private double finalNumber;

}
