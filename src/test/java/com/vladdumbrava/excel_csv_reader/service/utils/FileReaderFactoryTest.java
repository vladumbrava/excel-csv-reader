package com.vladdumbrava.excel_csv_reader.service.utils;

import java.util.List;

import com.vladdumbrava.excel_csv_reader.exception.ReaderNotFoundException;
import com.vladdumbrava.excel_csv_reader.service.utils.reader.CSVEmployeeFileReader;
import com.vladdumbrava.excel_csv_reader.service.utils.reader.EmployeeFileReader;
import com.vladdumbrava.excel_csv_reader.service.utils.reader.XLSXEmployeeFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FileReaderFactoryTest {

    private CSVEmployeeFileReader csvReader;
    private XLSXEmployeeFileReader xlsxReader;
    private FileReaderFactory factory;

    @BeforeEach
    void setUp() {
        csvReader = new CSVEmployeeFileReader();
        xlsxReader = new XLSXEmployeeFileReader();
        factory = new FileReaderFactory(List.of(csvReader, xlsxReader));
    }

    @Test
    void givenCsvFile_whenGetReader_thenReturnCsvReader() {
        MockMultipartFile file = new MockMultipartFile("file", "employees.csv", "text/csv", new byte[0]);

        EmployeeFileReader reader = factory.getReader(file);

        assertThat(reader).isInstanceOf(CSVEmployeeFileReader.class);
    }

    @Test
    void givenXlsxFile_whenGetReader_thenReturnXlsxReader() {
        MockMultipartFile file = new MockMultipartFile("file", "Employees.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);

        EmployeeFileReader reader = factory.getReader(file);

        assertThat(reader).isInstanceOf(XLSXEmployeeFileReader.class);
    }

    @Test
    void givenFileWithoutExtension_whenGetReader_thenThrowException() {
        MockMultipartFile file = new MockMultipartFile("file", "employees", "text/plain", new byte[0]);

        assertThatThrownBy(() -> factory.getReader(file))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("File has no valid extension");
    }

    @Test
    void givenUnsupportedExtension_whenGetReader_thenThrowException() {
        MockMultipartFile file = new MockMultipartFile("file", "employees.txt", "text/plain", new byte[0]);

        assertThatThrownBy(() -> factory.getReader(file))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Unsupported file extension");
    }

    @Test
    void givenMissingReaderInConstructor_whenInit_thenThrowReaderNotFound() {
        assertThatThrownBy(() -> new FileReaderFactory(List.of(csvReader))) // missing XLSX reader
                .isInstanceOf(ReaderNotFoundException.class)
                .hasMessageContaining("Reader not found for class");
    }

    @Test
    void givenNullFilename_whenGetReader_thenThrowException() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn(null);

        assertThatThrownBy(() -> factory.getReader(file))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("File has no valid extension: null");
    }

    @Test
    void givenFilenameWithoutExtension_whenGetReader_thenThrowException() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn("noextension");

        assertThatThrownBy(() -> factory.getReader(file))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("File has no valid extension: noextension");
    }

}
