package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {
    @Test
    void whenFormatOne() {
        String date = "2011-12-03T10:15:30+01:00";
        DateTimeParser parser = new HabrCareerDateTimeParser();
        assertThat(parser.parse(date)).isEqualTo("2011-12-03T10:15:30");
    }

    @Test
    void whenFormatTwo() {
        String date = "2011-12-03T10:15:30Z";
        DateTimeParser parser = new HabrCareerDateTimeParser();
        assertThat(parser.parse(date)).isEqualTo("2011-12-03T10:15:30");
    }

    @Test
    void whenWrongFormat() {
        String date = "2011-12-0310:15:32";
        DateTimeParser parser = new HabrCareerDateTimeParser();
        assertThatThrownBy(() -> parser.parse(date)).isInstanceOf(DateTimeParseException.class);
    }
}