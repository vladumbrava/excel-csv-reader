package com.vladdumbrava.excel_csv_reader.service.utils.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.vladdumbrava.excel_csv_reader.exception.FileProcessingException;
import com.vladdumbrava.excel_csv_reader.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class XLSXEmployeeFileReaderTest {

    private XLSXEmployeeFileReader reader;

    @BeforeEach
    void setUp() {
        reader = new XLSXEmployeeFileReader();
    }

    @Test
    void givenValidXlsx_whenRead_thenParseToEmployees() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Employees.xlsx");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "Employees.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                inputStream
        );

        List<Employee> employees = reader.read(file);

        assertThat(employees).hasSize(4);
        Employee employee = employees.getFirst();
        assertThat(employee.getName()).isEqualTo("Andreea");
    }

    @Test
    void givenEmptyXlsx_whenRead_thenThrowException() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("EmptyFile.xlsx");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "Employees.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                inputStream
        );

        assertThatThrownBy(() -> reader.read(file))
                .isInstanceOf(FileProcessingException.class)
                .hasMessageContaining("Excel file is missing header row.");
    }

    @Test
    void givenNonExistingXlsx_whenRead_thenThrowException() throws IOException {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockFile.getInputStream()).thenThrow(new IOException("Simulated IO error"));
        Mockito.when(mockFile.getOriginalFilename()).thenReturn("broken.xlsx");

        assertThatThrownBy(() -> reader.read(mockFile))
                .isInstanceOf(FileProcessingException.class)
                .hasMessageContaining("Failed to read Excel file");
    }

}
