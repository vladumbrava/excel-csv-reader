package com.vladdumbrava.excel_csv_reader.service.utils.reader;

import com.vladdumbrava.excel_csv_reader.model.Employee;
import com.vladdumbrava.excel_csv_reader.model.Gender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component("csvReader")
@Slf4j
public class CSVEmployeeFileReader implements EmployeeFileReader{

    //exception handling for case when headerlineparts is not equal to 7
    //more exception handling
    @Override
    public List<Employee> read(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                log.warn("CSV file is empty.");
                return new ArrayList<>();
            }
            String[] headerLineParts = headerLine.split(",");
            return reader.lines()
                    .map(line -> {
                        String[] parts = line.split(",");
                        if (parts.length != headerLineParts.length) {
                            log.error("Line: {}\n does not have the proper number of fields", line);
                        }
                        Employee employee = new Employee();
                        employee.setName(parts[0]);
                        employee.setAge(Integer.parseInt(parts[1]));
                        employee.setGender(Gender.valueOf(parts[2]));
                        employee.setRole(parts[3]);
                        employee.setEmail(parts[4]);
                        employee.setPhoneNumber(parts[5]);
                        employee.setActive(Boolean.valueOf(parts[6]));
                        return employee;
                    })
                    .toList();
        } catch (IOException e) {
            log.error("Failed to read CSV file.");
            throw new RuntimeException("Failed to read CSV file", e);
        }
    }
}
