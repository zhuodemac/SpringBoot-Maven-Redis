package com.example.service;

import com.example.dao.entity.UserInfo;
import com.example.dao.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final String CACHE_PREFIX = "user:";
    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public UserService(UserMapper userMapper, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public List<UserInfo> findAll() {
        log.info("Querying all users");
        return userMapper.findAll();
    }

    public UserInfo findById(Long id) {
        String key = CACHE_PREFIX + id;
        try {
            String cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.debug("Redis cache hit: userId={}", id);
                return objectMapper.readValue(cached, UserInfo.class);
            }
        } catch (Exception e) {
            log.warn("Redis cache read failed; falling back to MySQL: userId={}", id, e);
        }

        UserInfo user = userMapper.findById(id);
        if (user != null) {
            try {
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(user), Duration.ofMinutes(10));
            } catch (Exception e) {
                log.warn("Redis cache write failed: userId={}", id, e);
            }
        }
        return user;
    }

    @Transactional
    public UserInfo create(UserInfo user) {
        userMapper.insert(user);
        log.info("Created user: userId={}", user.getId());
        return user;
    }

    @Transactional
    public boolean update(UserInfo user) {
        boolean updated = userMapper.update(user) > 0;
        evictCache(user.getId());
        log.info("Updated user: userId={}, updated={}", user.getId(), updated);
        return updated;
    }

    @Transactional
    public boolean delete(Long id) {
        boolean deleted = userMapper.deleteById(id) > 0;
        evictCache(id);
        log.info("Deleted user: userId={}, deleted={}", id, deleted);
        return deleted;
    }

    private void evictCache(Long id) {
        try {
            redisTemplate.delete(CACHE_PREFIX + id);
        } catch (Exception e) {
            log.warn("Redis cache eviction failed: userId={}", id, e);
        }
    }
}
