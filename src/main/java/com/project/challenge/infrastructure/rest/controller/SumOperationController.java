package com.project.challenge.infrastructure.rest.controller;

import com.project.challenge.application.usecases.SumOperationService;
import com.project.challenge.infrastructure.client.dto.PercentageResponseDTO;
import com.project.challenge.infrastructure.rest.request.OperationDTORequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operation")
public class SumOperationController {

    @Autowired
    private SumOperationService sumOperationService;

    @GetMapping("/sum")
    public ResponseEntity<PercentageResponseDTO> calculate(@Valid  @RequestBody OperationDTORequest req){
        return ResponseEntity.ok(sumOperationService.apply(req));
    }

}
