package com.project.challenge.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.challenge.infrastructure.rest.request.OperationDTORequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SumOperationControllerTest {

    private static final String SUM_GET_URL = "/operation/sum";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test 1: calculate")
    public void test_Calculate_Should_Calculate_When_GiveTwoNumber() throws Exception {
        var sumNumeros = OperationDTORequest.builder().first((double) 1).second((double) 2).build();
        RequestBuilder request = prepareRequest(SUM_GET_URL, objectMapper.writeValueAsString(sumNumeros));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assertions.assertEquals(content, "{\"number1\":1,\"number2\":2,\"percentage\":10.0,\"final_number\":3.3}");

    }

    @Test
    @DisplayName("Test 2: calculate bad request")
    public void test_Calculate_Should_BadRequest_When_GiveOneNumber() throws Exception {
        var sumNumeros = OperationDTORequest.builder().first(null).second((double) 2).build();
        RequestBuilder request = prepareRequest(SUM_GET_URL, objectMapper.writeValueAsString(sumNumeros));
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }


    private RequestBuilder prepareRequest(final String url, final String sumRequest) {
        return MockMvcRequestBuilders
                .get(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(sumRequest);
    }
}