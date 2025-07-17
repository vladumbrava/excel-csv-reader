package com.vladdumbrava.excel_csv_reader.service.utils;

import com.vladdumbrava.excel_csv_reader.service.utils.reader.CSVEmployeeFileReader;
import com.vladdumbrava.excel_csv_reader.service.utils.reader.EmployeeFileReader;
import com.vladdumbrava.excel_csv_reader.service.utils.reader.XLSXEmployeeFileReader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FileReaderFactory {

    private final Map<String, EmployeeFileReader> readers;

    public FileReaderFactory(List<EmployeeFileReader> readerList) {
        this.readers = new HashMap<>();
        readers.put("csv", getReaderByType(readerList, CSVEmployeeFileReader.class));
        readers.put("xlsx", getReaderByType(readerList, XLSXEmployeeFileReader.class));
    }

    public EmployeeFileReader getReader(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            throw new UnsupportedOperationException("File has no valid extension: " + filename);
        }

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        if (!readers.containsKey(extension)) {
            throw new UnsupportedOperationException("Unsupported file extension: " + extension);
        }

        return readers.get(extension);
    }

    private EmployeeFileReader getReaderByType(List<EmployeeFileReader> list, Class<?> clazz) {
        return list.stream()
                .filter(clazz::isInstance)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Reader not found for class: " + clazz));
    }
}

