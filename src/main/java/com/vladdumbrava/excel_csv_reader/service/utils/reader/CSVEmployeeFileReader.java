package com.vladdumbrava.excel_csv_reader.service.utils.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import com.vladdumbrava.excel_csv_reader.exception.FileProcessingException;
import com.vladdumbrava.excel_csv_reader.model.Employee;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static com.vladdumbrava.excel_csv_reader.service.utils.DataTypeParser.*;

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
                            employee.setDateOfBirth(parseDate(parts[1]));
                            employee.setGender(parseGender(parts[2]));
                            employee.setRole(handleNullityInString(parts[3]));
                            employee.setEmail(handleNullityInString(parts[4]));
                            employee.setPhoneNumber(handleNullityInString(parts[5]));
                            employee.setActive(parseBoolean(parts[6]));
                            return employee;
                        }
                        catch (Exception e) {
                            log.error("Error parsing line: {}\n{}", line, e.getMessage());
                            throw new FileProcessingException("Failed to parse CSV line: " + line + "\n" + e.getMessage());
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

        }
        catch (IOException e) {
            log.error("Failed to read CSV file", e);
            throw new FileProcessingException("Failed to read CSV file");
        }
        catch (Exception e) {
            if (e instanceof FileProcessingException) {
                throw e;
            }
            log.error("Unexpected error while processing CSV file", e);
            throw new FileProcessingException("Unexpected error while processing CSV file");
        }
    }

}
