package com.vladdumbrava.excel_csv_reader.service.utils.reader;

import com.vladdumbrava.excel_csv_reader.exception.FileProcessingException;
import com.vladdumbrava.excel_csv_reader.model.Employee;
import com.vladdumbrava.excel_csv_reader.model.Gender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

@Component("csvReader")
@Slf4j
public class CSVEmployeeFileReader implements EmployeeFileReader{

    @Override
    public List<Employee> read(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            log.info("Starting to parse CSV file: {}", file.getOriginalFilename());

            String headerLine = reader.readLine();
            if (headerLine == null) {
                log.error("CSV file is empty.");
                throw new FileProcessingException("CSV file is empty.");
            }

            String[] headerLineParts = headerLine.split(",");
            int expectedFields = 7;

            if (headerLineParts.length != expectedFields) {
                log.error("CSV header is malformed. Expected {} fields, but got {}: {}",
                        expectedFields, headerLineParts.length, headerLine);
                throw new FileProcessingException("Invalid CSV header format.");
            }

            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isBlank())
                    .map(line -> {
                        String[] parts = line.split(",", -1);
                        if (parts.length != expectedFields) {
                            log.warn("Skipping malformed line: {}", line);
                            return null;
                        }

                        try {
                            Employee employee = new Employee();
                            employee.setName(handleNullityInString(parts[0]));
                            employee.setAge(parseInteger(parts[1]));
                            employee.setGender(parseGender(parts[2]));
                            employee.setRole(handleNullityInString(parts[3]));
                            employee.setEmail(handleNullityInString(parts[4]));
                            employee.setPhoneNumber(handleNullityInString(parts[5]));
                            employee.setActive(parseBooleanNullable(parts[6]));
                            return employee;
                        } catch (Exception e) {
                            log.error("Error parsing line: {}\n{}", line, e.getMessage());
                            throw new FileProcessingException("Failed to parse CSV line: " + line + "\n" + e.getMessage());
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

        } catch (IOException e) {
            log.error("Failed to read CSV file", e);
            throw new FileProcessingException("Failed to read CSV file");
        } catch (Exception e) {
            log.error("Unexpected error while processing CSV file");
            throw new FileProcessingException("Unexpected error while processing CSV file");
        }
    }

    private String handleNullityInString(String s) {
        return (s == null || s.isBlank() || s.equalsIgnoreCase("null") || s.equalsIgnoreCase("n/a"))
                ? null
                : s.trim();
    }

    private Integer parseInteger(String s) {
        try {
            return handleNullityInString(s) == null ? null : Integer.parseInt(handleNullityInString(s));
        } catch (NumberFormatException e) {
            log.warn("Invalid integer value: {}", s);
            return null;
        }
    }

    private Gender parseGender(String s) {
        try {
            return handleNullityInString(s) == null ? null : Gender.valueOf(handleNullityInString(s).toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid gender value: {}", s);
            return null;
        }
    }

    private Boolean parseBooleanNullable(String s) {
        String trimmed = handleNullityInString(s);
        if (trimmed == null) return null;
        return switch (trimmed.toLowerCase()) {
            case "true" -> true;
            case "false" -> false;
            default -> {
                log.warn("Invalid boolean value: {}", trimmed);
                yield null;
            }
        };
    }


}
