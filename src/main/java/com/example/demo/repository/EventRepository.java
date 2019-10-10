package com.example.demo.repository;

import com.example.demo.domain.input.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<List<Event>> findAllById(final String id);
}
