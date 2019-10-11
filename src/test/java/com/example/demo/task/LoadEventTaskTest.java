package com.example.demo.task;

import com.example.demo.TestConfig;
import com.example.demo.parser.EventParser;
import com.example.demo.repository.EventRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Set;

@ContextConfiguration(classes = {TestConfig.class})
@DataJpaTest
public class LoadEventTaskTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void testReadEventsAreLoadedInDb() throws Exception {

        LoadEventTask loadEventTask = new LoadEventTask(getClass().getClassLoader().getResource("logfile.txt").getPath(), eventRepository, new EventParser());
        Set<String> eventIds = loadEventTask.call();
        Assertions.assertThat(eventIds).containsExactly("scsmbstgra", "scsmbstgrb", "scsmbstgrc");
    }
}
