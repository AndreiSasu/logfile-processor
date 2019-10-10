package com.example.demo.service;

import com.example.demo.domain.input.Event;
import com.example.demo.domain.input.State;
import com.example.demo.domain.output.ProcessedEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class EventProcessorServiceTest {

    private static final int MAX_ALERT_THRESHOLD = 4;
    private EventProcessorService eventProcessorService = new EventProcessorService(MAX_ALERT_THRESHOLD);

    @Test
    public void testNullOrEmptyRawEvents() {
        List<Event> events = Arrays.asList(null, new Event());
        final Throwable throwable = Assertions.catchThrowable(() -> eventProcessorService.process(events));

        final String expectedMessage = "Invalid input received, raw event cannot be null";
        Assertions.assertThat(throwable).isInstanceOf(ProcessingException.class).hasMessageContaining(expectedMessage);
    }

    @Test
    public void testStartEventAfterEndEvent() {
        final Event startEvent = new Event();
        startEvent.setTimestamp(1491377495218L);
        startEvent.setState(State.STARTED);
        final Event endEvent = new Event();
        endEvent.setTimestamp(1491377495210L);
        endEvent.setState(State.FINISHED);

        List<Event> events = Arrays.asList(startEvent, endEvent);
        final Throwable throwable = Assertions.catchThrowable(() -> eventProcessorService.process(events));

        final String expectedMessage = "startEvent cannot be after endEvent";
        Assertions.assertThat(throwable).isInstanceOf(ProcessingException.class).hasMessageContaining(expectedMessage);
    }

    @Test
    public void testHappyPath() throws ProcessingException {

        final Event startEvent = new Event();
        startEvent.setTimestamp(1491377495210L);
        startEvent.setState(State.STARTED);
        startEvent.setId("test123");
        startEvent.setHost("some.host");

        final Event endEvent = new Event();
        endEvent.setTimestamp(1491377495218L);
        endEvent.setState(State.FINISHED);
        endEvent.setId("test123");
        endEvent.setHost("some.host");

        List<Event> events = Arrays.asList(startEvent, endEvent);
        ProcessedEvent processedEvent = eventProcessorService.process(events);
        Assertions.assertThat(processedEvent.getEventDuration().toMillis())
                .isEqualTo(endEvent.getTimestamp() - startEvent.getTimestamp());
        Assertions.assertThat(processedEvent.getHost()).isEqualTo("some.host");
        Assertions.assertThat(processedEvent.getEventId()).isEqualTo("test123");
    }

    @Test
    public void testHappyPathAlertFieldSet() throws ProcessingException {
        final Event startEvent = new Event();
        startEvent.setTimestamp(1491377495210L);
        startEvent.setState(State.STARTED);
        startEvent.setId("test123");
        startEvent.setHost("some.host");

        final Event endEvent = new Event();
        endEvent.setTimestamp(1491377495219L);
        endEvent.setState(State.FINISHED);
        endEvent.setId("test123");
        endEvent.setHost("some.host");

        List<Event> events = Arrays.asList(startEvent, endEvent);
        ProcessedEvent processedEvent = eventProcessorService.process(events);
        Assertions.assertThat(processedEvent.getEventDuration().toMillis())
                .isEqualTo(endEvent.getTimestamp() - startEvent.getTimestamp());

        Assertions.assertThat(processedEvent.isAlert()).isTrue();
        Assertions.assertThat(processedEvent.getHost()).isEqualTo("some.host");
        Assertions.assertThat(processedEvent.getEventId()).isEqualTo("test123");
    }

    @Test
    public void testAlertSentNoFinishEvent() throws ProcessingException {
        final Event startEvent = new Event();
        startEvent.setTimestamp(1491377495210L);
        startEvent.setState(State.STARTED);
        startEvent.setId("test123");
        startEvent.setHost("some.host");

        List<Event> events = Arrays.asList(startEvent);
        ProcessedEvent processedEvent = eventProcessorService.process(events);
        Assertions.assertThat(processedEvent.getEventDuration())
                .isEqualTo(Duration.ZERO);

        Assertions.assertThat(processedEvent.isAlert()).isTrue();
        Assertions.assertThat(processedEvent.getHost()).isEqualTo("some.host");
        Assertions.assertThat(processedEvent.getEventId()).isEqualTo("test123");
    }


}
