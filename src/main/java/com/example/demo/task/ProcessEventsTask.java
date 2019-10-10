package com.example.demo.task;

import com.example.demo.domain.input.Event;
import com.example.demo.domain.output.ProcessedEvent;
import com.example.demo.repository.ProcessedEventRepository;

import com.example.demo.repository.EventRepository;
import com.example.demo.service.Processor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
public class ProcessEventsTask implements Callable<Collection<ProcessedEvent>> {

    private final Collection<String> eventIds;
    private final Processor<Collection<Event>, ProcessedEvent> processor;
    private final EventRepository eventRepository;
    private final ProcessedEventRepository processedEventRepository;

    public ProcessEventsTask(final Collection<String> eventIds,
                             final Processor<Collection<Event>, ProcessedEvent> processor,
                             final EventRepository eventRepository,
                             final ProcessedEventRepository processedEventRepository) {
        this.eventIds = eventIds;
        this.processor = processor;
        this.eventRepository = eventRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Override
    public Collection<ProcessedEvent> call() throws Exception {

        Collection<ProcessedEvent> processedEvents = new ArrayList<>();

        for(String key: eventIds) {

            List<Event> events = eventRepository.findAllById(key).get();

            log.debug("Processing: {} ",events);

            ProcessedEvent processedEvent = processor.process(events);
            log.debug("Processed: " + processedEvent);

            ProcessedEvent savedEvent = processedEventRepository.save(processedEvent);
            processedEvents.add(savedEvent);
            log.debug("Saved: " + savedEvent);

        }

        log.debug("Processed {} events ", processedEvents.size());

        return processedEvents;

    }
}
