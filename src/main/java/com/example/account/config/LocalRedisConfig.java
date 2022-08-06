package com.example.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.Valid;

@Configuration
public class LocalRedisConfig {

    @Value("${spring.redis.port}")
    private int redisPort;  // 레디스를 띄워줄 포트를 넣어준다.

    private RedisServer redisServer;

    @PostConstruct

    public void startRedis(){
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis(){
        if(redisServer != null){    // 서버가 생성되면 스톱
            redisServer.stop();
        }
    }
}
