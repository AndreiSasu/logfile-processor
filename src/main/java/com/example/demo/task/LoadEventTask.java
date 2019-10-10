package com.example.demo.task;

import com.example.demo.domain.input.Event;
import com.example.demo.parser.ParseException;
import com.example.demo.parser.Parser;
import com.example.demo.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

@AllArgsConstructor
@Slf4j
public class LoadEventTask implements Callable<Set<String>> {

    private final String fileName;
    private final EventRepository eventRepository;
    private final Parser<String, Event> parser;

    @Override
    public Set<String> call() throws Exception {

        List<String> eventsAsString = Files.readAllLines(new File(fileName).toPath());
        log.info("Read file: {}, total lines: {}", fileName, eventsAsString.size());

        Set<String> readEventIds = new HashSet<>();
        eventsAsString.forEach(s -> {
            try {
                Event event = parser.parse(s);
                eventRepository.save(event);
                readEventIds.add(event.getId());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        return readEventIds;
    }
}
