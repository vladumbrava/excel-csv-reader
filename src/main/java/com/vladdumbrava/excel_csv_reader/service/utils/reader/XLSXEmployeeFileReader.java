package com.vladdumbrava.excel_csv_reader.service.utils.reader;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vladdumbrava.excel_csv_reader.exception.FileProcessingException;
import com.vladdumbrava.excel_csv_reader.model.Employee;
import com.vladdumbrava.excel_csv_reader.model.Gender;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component("xlsxReader")
@Slf4j
public class XLSXEmployeeFileReader implements EmployeeFileReader {

    @Override
    public List<Employee> read(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            log.info("Opened Excel file for reading: {}", file.getOriginalFilename());

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                log.error("No sheet found in Excel file.");
                throw new FileProcessingException("No sheet found in Excel file.");
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                log.error("Excel file has no header row.");
                throw new FileProcessingException("Excel file is missing header row.");
            }

            int totalRows = sheet.getLastRowNum();
            log.info("Processing Excel sheet with {} data rows.", totalRows);

            List<Employee> employees = IntStream.rangeClosed(1, totalRows)
                    .mapToObj(sheet::getRow)
                    .filter(Objects::nonNull)
                    .map(row -> {
                        try {
                            String name = handleNullityInString(getCellAsString(row, 0));
                            LocalDate dateOfBirth = parseDate(getCellAsString(row, 1));
                            Gender gender = parseGender(getCellAsString(row, 2));
                            String role = handleNullityInString(getCellAsString(row, 3));
                            String email = handleNullityInString(getCellAsString(row, 4));
                            String phone = handleNullityInString(getCellAsString(row, 5));
                            Boolean active = parseBooleanNullable(getCellAsString(row, 6));

                            Employee employee = new Employee();
                            employee.setName(name);
                            employee.setDateOfBirth(dateOfBirth);
                            employee.setGender(gender);
                            employee.setRole(role);
                            employee.setEmail(email);
                            employee.setPhoneNumber(phone);
                            employee.setActive(active);

                            log.info("Parsed employee from row {}: {}", row.getRowNum(), employee);
                            return employee;

                        }
                        catch (Exception e) {
                            log.error("Failed to parse row {}: {}", row.getRowNum(), e.getMessage());
                            throw new FileProcessingException("Failed to parse Excel row " + row.getRowNum() + ": " + e.getMessage());
                        }
                    })
                    .collect(Collectors.toList());

            log.info("Successfully read {} employee records from Excel file.", employees.size());
            return employees;

        }
        catch (IOException e) {
            log.error("Error reading Excel file: {}", file.getOriginalFilename(), e);
            throw new FileProcessingException("Failed to read Excel file: " + file.getOriginalFilename());
        }
        catch (Exception e) {
            log.error("Unexpected error while processing Excel file.");
            throw new FileProcessingException("Unexpected error while processing Excel file.");
        }
    }

    private String getCellAsString(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue()).trim();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue()).trim();
            case BLANK -> "N/A";
            default -> "UNKNOWN";
        };
    }

    private String handleNullityInString(String s) {
        return ((s == null || s.isBlank() ||
                s.equalsIgnoreCase("null") || s.equalsIgnoreCase("n/a"))
                ? null
                : s.trim());
    }

    private LocalDate parseDate(String s) {
        try {
            return (handleNullityInString(s) == null ? null : LocalDate.parse(handleNullityInString(s)));
        } catch (DateTimeParseException e) {
            log.warn("Invalid date format: {}", s);
            return null;
        }
    }

    private Gender parseGender(String s) {
        try {
            return (handleNullityInString(s) == null ? null : Gender.valueOf(handleNullityInString(s).toUpperCase()));
        }
        catch (IllegalArgumentException e) {
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
