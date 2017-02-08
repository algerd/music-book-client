
package ru.javafx.musicbook.client.utils;

import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateStringConverter extends StringConverter<LocalDate> {
    
	private final String pattern = "MM.dd.yyyy";
	private final DateTimeFormatter dtFormatter;

	public LocalDateStringConverter() {
		dtFormatter = DateTimeFormatter.ofPattern(pattern);
	}
    
    public String getPattern() {
        return pattern;
    }

	@Override
	public LocalDate fromString(String text) {
		LocalDate date = null;
		if (text != null && !text.trim().isEmpty()) {
			date = LocalDate.parse(text, dtFormatter);
		}
		return date;
	}

	@Override
	public String toString(LocalDate date) {
		String text = null;
		if (date != null) {
			text = dtFormatter.format(date);
		}
		return text;
	}
}
