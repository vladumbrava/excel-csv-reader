package com.vladdumbrava.excel_csv_reader.service.utils;

import java.time.LocalDate;

import com.vladdumbrava.excel_csv_reader.model.Gender;
import org.junit.jupiter.api.Test;

import static com.vladdumbrava.excel_csv_reader.service.utils.DataTypeParser.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DataTypeParserTest {

    @Test
    void givenString_whenHandleNullity_thenReturnTrimmed() {
        String input = " test ";

        String result = handleNullityInString(input);

        assertThat(result).isEqualTo(input.trim());
    }

    @Test
    void givenNullString_whenHandleNullity_thenReturnNull() {
        String input = null;

        String result = handleNullityInString(input);

        assertThat(result).isNull();
    }

    @Test
    void givenBlankString_whenHandleNullity_thenReturnNull() {
        String input = "";

        String result = handleNullityInString(input);

        assertThat(result).isNull();
    }

    @Test
    void givenQuoteOnQuoteNullString_whenHandleNullity_thenReturnNull() {
        String input = " null ";

        String result = handleNullityInString(input);

        assertThat(result).isNull();
    }

    @Test
    void givenQuoteOnQuoteNAString_whenHandleNullity_thenReturnNull() {
        String input = " n/a ";

        String result = handleNullityInString(input);

        assertThat(result).isNull();
    }

    @Test
    void givenString_whenParseDate_thenReturnDate() {
        LocalDate inputDate = LocalDate.now();
        String inputString = inputDate.toString();

        LocalDate result = parseDate(inputString);

        assertThat(result).isEqualTo(inputDate);
    }

    @Test
    void givenNullString_whenParseDate_thenReturnNull() {
        String input = null;

        LocalDate result = parseDate(input);

        assertThat(result).isNull();
    }

    @Test
    void givenBadString_whenParseDate_thenReturnNull() {
        String input = "test";

        LocalDate result = parseDate(input);

        assertThat(result).isNull();
    }

    @Test
    void givenString_whenParseGender_thenReturnGender() {
        String input = "male";

        Gender result = parseGender(input);

        assertThat(result).isEqualTo(Gender.MALE);
    }

    @Test
    void givenNullString_whenParseGender_thenReturnNull() {
        String input = null;

        Gender result = parseGender(input);

        assertThat(result).isNull();
    }

    @Test
    void givenBadString_whenParseGender_thenReturnNull() {
        String input = "test";

        Gender result = parseGender(input);

        assertThat(result).isNull();
    }

    @Test
    void givenTrueString_whenParseBoolean_thenReturnTrue() {
        String input = " true ";

        Boolean result = parseBoolean(input);

        assertThat(result).isTrue();
    }

    @Test
    void givenFalseString_whenParseBoolean_thenReturnFalse() {
        String input = " false ";

        Boolean result = parseBoolean(input);

        assertThat(result).isFalse();
    }

    @Test
    void givenNullString_whenParseBoolean_thenReturnNull() {
        String input = null;

        Boolean result = parseBoolean(input);

        assertThat(result).isNull();
    }

    @Test
    void givenBadString_whenParseBoolean_thenReturnNull() {
        String input = "test";

        Boolean result = parseBoolean(input);

        assertThat(result).isNull();
    }

}
