package com.example.demo.parser;

import com.example.demo.domain.input.Event;
import com.example.demo.domain.input.State;
import com.example.demo.domain.input.Type;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class RawEventParserTest {

    private static final String APPLICATION_LOG_EVENT = "{\"id\":\"scsmbstgra\", \"state\":\"STARTED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495212}";
    private static final String GENERIC_EVENT = "{\"id\":\"scsmbstgrb\", \"state\":\"STARTED\", \"timestamp\":1491377495213}";

    @Test
    public void testParse2EventTypes() throws ParseException {
        EventParser eventParser = new EventParser();
        Event applicationEvent = eventParser.parse(APPLICATION_LOG_EVENT);
        Assertions.assertThat(applicationEvent.getHost()).isEqualTo("12345");
        Assertions.assertThat(applicationEvent.getId()).isEqualTo("scsmbstgra");
        Assertions.assertThat(applicationEvent.getState()).isEqualTo(State.STARTED);
        Assertions.assertThat(applicationEvent.getType()).isEqualTo(Type.APPLICATION_LOG);
        Assertions.assertThat(applicationEvent.getTimestamp()).isEqualTo(1491377495212L);


        Event genericEvent = eventParser.parse(GENERIC_EVENT);

        Assertions.assertThat(genericEvent.getId()).isEqualTo("scsmbstgrb");
        Assertions.assertThat(genericEvent.getState()).isEqualTo(State.STARTED);
        Assertions.assertThat(genericEvent.getTimestamp()).isEqualTo(1491377495213L);
    }

    @Test
    public void testParseExceptionThrownForInvalidInput() throws ParseException {
        EventParser eventParser = new EventParser();
        final String invalidInput = "{this is invalid}";
        final Throwable throwable = Assertions.catchThrowable(() -> eventParser.parse(invalidInput));

        Assertions.assertThat(throwable)
                .isInstanceOf(ParseException.class)
                .hasMessageContaining("Failed to parse input: " + invalidInput);

    }
}
