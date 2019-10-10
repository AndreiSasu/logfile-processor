package com.example.demo;

import com.example.demo.domain.input.Event;
import com.example.demo.domain.output.ProcessedEvent;
import com.example.demo.parser.Parser;
import com.example.demo.repository.ProcessedEventRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.service.Processor;
import com.example.demo.task.LoadEventTask;
import com.example.demo.task.ProcessEventsTask;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootApplication
@Slf4j
public class DemoApplication implements ApplicationRunner {

    private final Parser<String, Event> parser;
    private final Processor<Collection<Event>, ProcessedEvent> processor;
    private final ProcessedEventRepository processedEventRepository;
    private final EventRepository eventRepository;
    private final int maxBatchSize;
    private final ExecutorService executorService;

    public DemoApplication(@Value("${demo.max.threads.pool}") int maxThreads,
                           @Value("${demo.max.events.batch.size}") int maxBatchSize,
                           Parser<String, Event> parser,
                           Processor<Collection<Event>, ProcessedEvent> processor,
                           ProcessedEventRepository processedEventRepository,
                           EventRepository eventRepository) {

    	this.parser = parser;
        this.processor = processor;
        this.processedEventRepository = processedEventRepository;
        this.maxBatchSize = maxBatchSize;
        this.eventRepository = eventRepository;

        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    	if(args.getNonOptionArgs().size() < 1) {
    		throw new IllegalArgumentException("Please specify folder input path.");
		}

    	final String folderInputPath = args.getNonOptionArgs().get(0);

        final List<String> files = readFileNamesFromInputFolder(folderInputPath);

        // Load the content of every file into a temporary table
        final Set<String> allEventIds = loadEvents(files);
        log.info("Loaded {} events in total.", allEventIds.size());

        //process all loaded events and load them into a second table
        final List<ProcessedEvent> processedEvents = processEvents(allEventIds);
        long totalAlerts = processedEvents.stream().filter(processedEventSequence -> processedEventSequence.isAlert()).count();

        log.info("Processed: {} events, {} alerts", processedEvents.size(), totalAlerts);

        executorService.shutdown();
    }


    private List<String> readFileNamesFromInputFolder(final String inputFolder) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(inputFolder))) {

            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());
            return result;
        }
    }

    private Set<String> loadEvents(final List<String> fileNames) throws ExecutionException, InterruptedException {

        final List<Future<Set<String>>> futureLoadedEventIds = new ArrayList<>();

        for (final String fileName : fileNames) {

            LoadEventTask loadFile = new LoadEventTask(fileName, eventRepository, parser);
            Future<Set<String>> loadedIdsFuture = executorService.submit(loadFile);
            futureLoadedEventIds.add(loadedIdsFuture);

        }

        final Set<String> allEventIds = new HashSet<>();
        for (Future<Set<String>> loadedIds : futureLoadedEventIds) {
            allEventIds.addAll(loadedIds.get());
        }

        return allEventIds;
    }

    private List<ProcessedEvent> processEvents(final Set<String> eventIds) throws ExecutionException, InterruptedException {

        final List<Future<Collection<ProcessedEvent>>> futureProcessedEvents = new ArrayList<>();

        Iterable<List<String>> partitionedBatches = Iterables.partition(eventIds, maxBatchSize);
        for (List<String> eventBatch : partitionedBatches) {
            ProcessEventsTask processEvents = new ProcessEventsTask(eventBatch, processor, eventRepository, processedEventRepository);
            futureProcessedEvents.add(executorService.submit(processEvents));
        }

        List<ProcessedEvent> processedEventSequences = new ArrayList<>();
        for (Future<Collection<ProcessedEvent>> processedEvents : futureProcessedEvents) {
            processedEventSequences.addAll(processedEvents.get());
        }

        return processedEventSequences;
    }
}
