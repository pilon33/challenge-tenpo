package com.project.challenge.application.usecases;

import com.project.challenge.application.adapter.PercentageService;
import com.project.challenge.infrastructure.client.dto.PercentageResponseDTO;
import com.project.challenge.infrastructure.rest.request.OperationDTORequest;
import org.springframework.stereotype.Service;

@Service
public class SumOperationService {

    private final PercentageService percentageService;

    public SumOperationService(PercentageService percentageService) {
        this.percentageService = percentageService;
    }

    public PercentageResponseDTO apply(OperationDTORequest request) {
        var summed = getSummedValues(request);
        var percentage = percentageService.calculate();
        var total= addPercentage(summed, percentage);
        return new PercentageResponseDTO(request.getFirst().intValue(),request.getSecond().intValue(),percentage,total);

    }

    private Double getSummedValues(OperationDTORequest request){
        return request.getFirst() + request.getSecond();
    }

    private Double addPercentage(Double summed, Double percentage){
        return summed + summed * (percentage / 100);
    }
}
