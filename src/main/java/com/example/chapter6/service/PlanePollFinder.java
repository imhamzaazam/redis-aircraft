package com.example.chapter6.service;

import com.example.chapter6.repository.AircraftRepository;
import com.example.chapter6.sburredis.Aircraft;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

@EnableScheduling
@Component
public class PlanePollFinder {


    private WebClient client = WebClient.create("http://localhost:3000/aircraft");
    private final RedisConnectionFactory connectionFactory;
    private final AircraftRepository aircraftRepository;

    public PlanePollFinder(RedisConnectionFactory connectionFactory, AircraftRepository aircraftRepository) {
        this.connectionFactory = connectionFactory;
        this.aircraftRepository = aircraftRepository;
    }

    @Scheduled(fixedRate = 1000)
    private void pollPlanes(){
        connectionFactory.getConnection().serverCommands().flushDb();
        client.get().retrieve()
                .bodyToFlux(Aircraft.class)
                .filter(plane -> !plane.getReg().isEmpty())
                .toStream()
                .forEach(aircraftRepository::save);

        aircraftRepository.findAll().forEach(System.out::println);
    }

}
