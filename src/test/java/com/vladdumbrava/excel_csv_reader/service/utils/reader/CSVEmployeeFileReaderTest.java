package com.vladdumbrava.excel_csv_reader.service.utils.reader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import com.vladdumbrava.excel_csv_reader.exception.FileProcessingException;
import com.vladdumbrava.excel_csv_reader.model.Employee;
import com.vladdumbrava.excel_csv_reader.model.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CSVEmployeeFileReaderTest {

    private CSVEmployeeFileReader reader;

    @BeforeEach
    void setUp() {
        reader = new CSVEmployeeFileReader();
    }

    @Test
    void givenValidCsv_whenRead_thenParseToEmployees() {
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
    void givenEmptyCsv_whenRead_thenThrowException() {
        MockMultipartFile file = new MockMultipartFile("file", "employees.csv",
                "text/csv", new byte[0]);

        assertThatThrownBy(() -> reader.read(file))
                .isInstanceOf(FileProcessingException.class)
                .hasMessageContaining("CSV file is empty");
    }

    @Test
    void givenBadHeaderCsv_whenRead_thenThrowException() {
        String csv = "name,dateOfBirth,gender\n" +
                "John Doe,1990-01-01,MALE";

        MockMultipartFile file = new MockMultipartFile("file", "employees.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> reader.read(file))
                .isInstanceOf(FileProcessingException.class)
                .hasMessageContaining("Invalid CSV header format");
    }

    @Test
    void givenUnexpectedFieldsLineCsv_whenRead_thenPassLine() {
        String csv = "name,dateOfBirth,gender,role,email,phoneNumber,active\n" +
                "John Doe,1990-01-01,MALE,Engineer,john@example.com";

        MockMultipartFile file = new MockMultipartFile("file", "employees.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        List<Employee> employees = reader.read(file);
        assertThat(employees).isEmpty();
    }

    @Test
    void givenNonExistingCsv_whenRead_thenThrowException() throws IOException {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockFile.getInputStream()).thenThrow(new IOException("Simulated IO error"));
        Mockito.when(mockFile.getOriginalFilename()).thenReturn("broken.csv");

        assertThatThrownBy(() -> reader.read(mockFile))
                .isInstanceOf(FileProcessingException.class)
                .hasMessageContaining("Failed to read CSV file");
    }


}
