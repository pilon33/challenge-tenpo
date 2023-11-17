package com.project.challenge.application.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.challenge.infrastructure.rest.configuration.CacheFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public CacheService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> void addToCache(String key, T value) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value));
        } catch (Exception e) {
            log.error("Cache write operation failed. {}", e.getMessage());
            throw new CacheFailureException(e.getMessage());
        }
    }
    public <T> Optional<T> getFromCache(String key, Class<T> type) {
        try {
            String percentageJsonString = redisTemplate.opsForValue().get(key);
            if (percentageJsonString != null)
                return Optional.of(objectMapper.readValue(percentageJsonString, type));
            return Optional.empty();
        } catch (Exception e) {
            log.error("Cache read operation failed. {}", e.getMessage());
            throw new CacheFailureException(e.getMessage());
        }
    }

    public Long incrementCounter(String key) {
        try {
            return redisTemplate.opsForValue().increment(key, 1);
        } catch (Exception e) {
            log.error("Cache increment counter operation failed. {}", e.getMessage());
            throw new CacheFailureException(e.getMessage());
        }
    }

    public void setExpirationTime(String key, int expirationTimeMinutes) {
        try {
            redisTemplate.expire(key, expirationTimeMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Cache set expiration time operation failed. {}", e.getMessage());
            throw new CacheFailureException(e.getMessage());
        }
    }

}
