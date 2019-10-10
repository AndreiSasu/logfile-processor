package com.example.demo.parser;

import com.example.demo.domain.input.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class EventParser implements Parser<String, Event> {

    private final ObjectMapper objectMapper;

    public EventParser() {
        this.objectMapper = new ObjectMapper();
    }


    @Override
    public Event parse(String input) throws ParseException {
        try {
            log.debug("Parsing: {}", input);
            Event event = this.objectMapper.readValue(input, Event.class);
            log.debug("Parsed raw event: {}", event);
            return event;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ParseException("Failed to parse input: " + input, e);
        }
    }
}
