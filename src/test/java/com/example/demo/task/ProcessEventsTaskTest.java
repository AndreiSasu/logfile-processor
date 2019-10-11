package com.example.demo.task;

import com.example.demo.TestConfig;
import com.example.demo.domain.input.Event;
import com.example.demo.domain.input.State;
import com.example.demo.domain.output.ProcessedEvent;
import com.example.demo.parser.EventParser;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.ProcessedEventRepository;
import com.example.demo.service.Processor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ContextConfiguration(classes = {TestConfig.class})
@DataJpaTest
public class ProcessEventsTaskTest {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @Autowired
    private Processor<Collection<Event>, ProcessedEvent> processor;

    private Set<String> loadedEventIDs = new HashSet<>();

    @BeforeEach
    public void setup() throws Exception {
        Event startEvent1 = new Event();
        startEvent1.setHost("test");
        startEvent1.setId("testid");
        startEvent1.setState(State.STARTED);
        startEvent1.setTimestamp(1231231L);

        Event endEvent1 = new Event();
        endEvent1.setHost("test");
        endEvent1.setId("testid");
        endEvent1.setState(State.FINISHED);
        endEvent1.setTimestamp(1231231L);

        Event startEvent2 = new Event();
        startEvent2.setHost("test");
        startEvent2.setId("testid2");
        startEvent2.setState(State.STARTED);
        startEvent2.setTimestamp(1231231L);

        Event endEvent2 = new Event();
        endEvent2.setHost("test");
        endEvent2.setId("testid2");
        endEvent2.setState(State.FINISHED);
        endEvent2.setTimestamp(1231231L);

        eventRepository.saveAll(Arrays.asList(new Event[]{startEvent1, startEvent2, endEvent1, endEvent2}));
        loadedEventIDs.addAll(eventRepository.findAll().stream().map(event -> event.getId()).collect(Collectors.toSet()));
    }

    @Test
    public void testProcessedEventsAreLoadedInDb() throws Exception {
        ProcessEventsTask processEventsTask = new ProcessEventsTask(loadedEventIDs, processor, eventRepository, processedEventRepository);
        Collection<ProcessedEvent> processedEvents = processEventsTask.call();
        Assertions.assertThat(processedEvents)
                .hasSize(2)
                .allMatch(processedEvent -> processedEvent.getEventDuration() != null, "Duration not found!")
                .allMatch(processedEvent -> "test".equals(processedEvent.getHost()), "Host Not Found!")
                .allMatch(processedEvent -> processedEvent.getEventId() != null, "Event ID not found!");
    }
}
