package org.example.task7.utility;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String DATE_PATTERN = "dd.MM.yyyy";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
}
