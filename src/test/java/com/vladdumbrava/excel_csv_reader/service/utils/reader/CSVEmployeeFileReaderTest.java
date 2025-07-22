package com.vladdumbrava.excel_csv_reader.service.utils.reader;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import com.vladdumbrava.excel_csv_reader.exception.FileProcessingException;
import com.vladdumbrava.excel_csv_reader.model.Employee;
import com.vladdumbrava.excel_csv_reader.model.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CSVEmployeeFileReaderTest {

    private CSVEmployeeFileReader reader;

    @BeforeEach
    void setUp() {
        reader = new CSVEmployeeFileReader();
    }

    @Test
    void testReadValidCsv() {
        String csv = "name,dateOfBirth,gender,role,email,phoneNumber,active\n" +
                "John Doe,1990-01-01,MALE,Engineer,john@example.com,1234567890,true";

        MockMultipartFile file = new MockMultipartFile("file", "employees.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        List<Employee> employees = reader.read(file);

        assertThat(employees).hasSize(1);
        Employee employee = employees.getFirst();
        assertThat(employee.getName()).isEqualTo("John Doe");
        assertThat(employee.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(employee.getGender()).isEqualTo(Gender.MALE);
        assertThat(employee.getRole()).isEqualTo("Engineer");
        assertThat(employee.getEmail()).isEqualTo("john@example.com");
        assertThat(employee.getPhoneNumber()).isEqualTo("1234567890");
        assertThat(employee.getActive()).isTrue();
    }

    @Test
    void testReadCsvWithEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "employees.csv",
                "text/csv", new byte[0]);

        assertThatThrownBy(() -> reader.read(file))
                .isInstanceOf(FileProcessingException.class)
                .hasMessageContaining("CSV file is empty");
    }

    @Test
    void testReadCsvWithMalformedHeader() {
        String csv = "name,dateOfBirth,gender\n" +
                "John Doe,1990-01-01,MALE";

        MockMultipartFile file = new MockMultipartFile("file", "employees.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> reader.read(file))
                .isInstanceOf(FileProcessingException.class)
                .hasMessageContaining("Invalid CSV header format");
    }

    @Test
    void testReadCsvWithMalformedLine() {
        String csv = "name,dateOfBirth,gender,role,email,phoneNumber,active\n" +
                "John Doe,1990-01-01,MALE,Engineer,john@example.com"; // only 6 fields

        MockMultipartFile file = new MockMultipartFile("file", "employees.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        List<Employee> employees = reader.read(file);
        assertThat(employees).isEmpty();
    }

    @Test
    void testReadCsvWithInvalidDateAndGender() {
        String csv = "name,dateOfBirth,gender,role,email,phoneNumber,active\n" +
                "Jane Doe,invalid_date,invalid_gender,Manager,jane@example.com,1234567890,false";

        MockMultipartFile file = new MockMultipartFile("file", "employees.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        List<Employee> employees = reader.read(file);
        assertThat(employees).hasSize(1);
        Employee employee = employees.getFirst();
        assertThat(employee.getDateOfBirth()).isNull();
        assertThat(employee.getGender()).isNull();
    }

    @Test
    void testReadCsvWithInvalidBoolean() {
        String csv = "name,dateOfBirth,gender,role,email,phoneNumber,active\n" +
                "Jake Smith,1995-05-10,MALE,Developer,jake@example.com,1234567890,maybe";

        MockMultipartFile file = new MockMultipartFile("file", "employees.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        List<Employee> employees = reader.read(file);
        assertThat(employees).hasSize(1);
        Employee employee = employees.getFirst();
        assertThat(employee.getActive()).isNull();
    }

}
