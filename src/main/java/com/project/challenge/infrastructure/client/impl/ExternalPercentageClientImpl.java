package com.project.challenge.infrastructure.client.impl;

import com.project.challenge.application.adapter.PercentageService;
import com.project.challenge.application.usecases.CacheService;
import com.project.challenge.infrastructure.client.dto.CachedPercentage;
import com.project.challenge.infrastructure.client.dto.PercentageResponseDTO;
import com.project.challenge.infrastructure.rest.configuration.HttpRequestException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Service
@Slf4j
public class ExternalPercentageClientImpl implements PercentageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalPercentageClientImpl.class);

    public static final String PERCENTAGE_CACHE = "PERCENTAGE_CACHE";

    @Value("10")
    private long cachedPercentageValidTime;

    private final WebClient webClient = WebClient.create("https://mocki.io");

    @Autowired
    private Map<String, Double> percentageMap;

    private final CacheService cacheService;

    public ExternalPercentageClientImpl(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public Double calculate() {
        var percentageCache = this.percentageMap.get(PERCENTAGE_CACHE);

        if (percentageCache != null && getFromCache() != null) {
            cacheService.addToCache(PERCENTAGE_CACHE, new CachedPercentage(percentageCache));

            return percentageCache;
        }else{
            return this.recoverFromServer();
        }
    }

    public Double getFromCache() {
        log.info("Get percentage from cache");
        return cacheService.getFromCache(PERCENTAGE_CACHE, CachedPercentage.class)
                .map(this::validateCachedPercentageTime)
                .orElseGet(() ->{
                    log.info("There is no cached percentage");
                    return recoverFromServer();
                });
    }
    private double validateCachedPercentageTime(CachedPercentage cachedPercentage) {
        log.info("Validate percentage obtained from cache. {}", cachedPercentage);
        return cachedPercentageIsValid(cachedPercentage) ?
                cachedPercentage.getPercentage() :
                fetchPercentageWithFallback(cachedPercentage);
    }

    private double fetchPercentageWithFallback(CachedPercentage cachedPercentage) {
        try {
            return recoverFromServer();
        } catch (HttpRequestException e) {
            log.warn("An error occurred fetching percentage. {}", e.getMessage());
            return cachedPercentage.getPercentage();
        }
    }

    private boolean cachedPercentageIsValid(CachedPercentage cachedPercentage) {
        OffsetDateTime currentUtcTime = OffsetDateTime.now(ZoneOffset.UTC);
        return cachedPercentage.getCreatedTime().plusMinutes(cachedPercentageValidTime).isAfter(currentUtcTime);
    }

    public Double recoverFromServer() {
        var response = retrievePercentage();
        if (response.blockOptional().isPresent()){
            var entity = response.block();
            return entity.getPercentage();
        }
        return null;
    }

    @Retry(name = "externalRetry",fallbackMethod = "findLastInCache")
    public Mono<PercentageResponseDTO> retrievePercentage() {
        LOGGER.info("GOLPEANDO AL SERVIDOR");
        return webClient.get()
                    .uri("/v1/223e5883-e12e-428e-bfe9-e5df61a85fc7")
                    .retrieve()
                    .bodyToMono(PercentageResponseDTO.class)
                    .doOnNext(response -> this.percentageMap.put(PERCENTAGE_CACHE, response.getPercentage()));
    }

    private Mono<PercentageResponseDTO> findLastInCache(Exception ex) {
        var percentageCache = this.percentageMap.get(PERCENTAGE_CACHE);

        if (percentageCache != null) {
            return Mono.just(PercentageResponseDTO.builder().percentage(percentageCache).build());
        }else{
            return null;
        }
    }
}
