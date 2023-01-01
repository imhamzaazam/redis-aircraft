package com.example.chapter6.repository;

import com.example.chapter6.sburredis.Aircraft;
import org.springframework.data.repository.CrudRepository;

public interface AircraftRepository extends CrudRepository<Aircraft, Long> {
}
