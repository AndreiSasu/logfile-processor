package com.example.demo.service;

import com.example.demo.domain.input.Event;
import com.example.demo.domain.input.State;
import com.example.demo.domain.output.ProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class EventProcessorService implements Processor<Collection<Event>, ProcessedEvent> {

    private final int maxAlertThresholdMs;

    public EventProcessorService(@Value("${demo.max.alert.threshold.ms}") int maxAlertThresholdMs) {
        this.maxAlertThresholdMs = maxAlertThresholdMs;
    }

    @Override
    public ProcessedEvent process(final Collection<Event> input) throws ProcessingException {
        validateInput(input);

        //we only need to get start and finish events
        final Event start = input.stream().filter(rawEvent -> rawEvent.getState() == State.STARTED)
                .findFirst()
                .orElseThrow(()-> new ProcessingException("No Start event found "));

        Optional<Event> endEvent = input.stream().filter(rawEvent -> rawEvent.getState() == State.FINISHED)
                .findFirst();

        if(!endEvent.isPresent()) {
            return ProcessedEvent.builder()
                    .eventId(start.getId())
                    .eventDuration(Duration.ZERO)
                    .alert(true)
                    .host(start.getHost())
                    .build();
        }


        return doProcess(start, endEvent.get());
    }

    private ProcessedEvent doProcess(final Event startEvent, final Event endEvent) throws ProcessingException {
        final long durationDiff = endEvent.getTimestamp() - startEvent.getTimestamp();

        if(durationDiff < 0) {
            throw new ProcessingException(String.format("startEvent cannot be after endEvent: %s, %s", startEvent, endEvent));
        }

        final boolean shouldAlert = durationDiff > maxAlertThresholdMs;
        if(shouldAlert) log.warn("Alert for events: \n {} \n {}", startEvent, endEvent);
        final Duration duration = Duration.ofMillis(durationDiff);
        return ProcessedEvent.builder()
                .eventId(startEvent.getId())
                .eventDuration(duration)
                .alert(shouldAlert)
                .host(startEvent.getHost())
                .build();
    }

    private void validateInput(final Collection<Event> events) throws ProcessingException {
        //also does null check
        if(CollectionUtils.isEmpty(events)) {
            throw new ProcessingException("Invalid input received, raw events cannot be empty");
        }

        for(Event event : events) {
            if(Objects.isNull(event))
                throw new ProcessingException(String.format("Invalid input received, raw event cannot be null: %s", events));
        }
    }


}
