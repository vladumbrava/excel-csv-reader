package com.vladdumbrava.excel_csv_reader.service.utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.vladdumbrava.excel_csv_reader.model.Gender;

public class DataTypeParser {

    public static String handleNullityInString(String s) {
        return ((s == null || s.isBlank() ||
                s.trim().equalsIgnoreCase("null") || s.trim().equalsIgnoreCase("n/a"))
                ? null
                : s.trim());
    }

    public static LocalDate parseDate(String s) {
        try {
            return (handleNullityInString(s) == null ? null : LocalDate.parse(handleNullityInString(s)));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static Gender parseGender(String s) {
        try {
            return (handleNullityInString(s) == null ? null : Gender.valueOf(handleNullityInString(s).toUpperCase()));
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Boolean parseBoolean(String s) {
        String trimmed = handleNullityInString(s);
        if (trimmed == null) return null;
        return switch (trimmed.toLowerCase()) {
            case "true" -> true;
            case "false" -> false;
            default -> null;
        };
    }

}
