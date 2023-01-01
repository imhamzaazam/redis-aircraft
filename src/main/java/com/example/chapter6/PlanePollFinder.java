package com.example.chapter6;

import com.example.chapter6.sburredis.Aircraft;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@EnableScheduling
@Component
public class PlanePollFinder {
    private WebClient client = WebClient.create("http://localhost:3000/aircraft");
    private final RedisConnectionFactory connectionFactory;
    private final RedisOperations<String, Aircraft> redisOperations;

    public PlanePollFinder(RedisConnectionFactory ConnectionFactory, RedisOperations<String, Aircraft> redisOperations) {
        this.connectionFactory = ConnectionFactory;
        this.redisOperations = redisOperations;
    }

    @Scheduled(fixedRate = 1000)
    private void pollPlanes(){
        connectionFactory.getConnection().serverCommands().flushDb();
        client.get().retrieve()
                .bodyToFlux(Aircraft.class)
                .filter(plane -> !plane.getReg().isEmpty())
                .toStream()
                .forEach(it -> redisOperations.opsForValue().set(it.getReg(), it));

        redisOperations.opsForValue()
                .getOperations()
                .keys("*")
                .forEach(it ->
                        System.out.println(redisOperations.opsForValue().get(it)));
    }

}
