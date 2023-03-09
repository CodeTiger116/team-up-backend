package com.hanhu.teamupbackend.service;


import com.hanhu.teamupbackend.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void test() {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        valueOperations.set("test", "dog");

    }

    @Test
    void test2(){
        ValueOperations valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set("test2","dog");
    }
}
