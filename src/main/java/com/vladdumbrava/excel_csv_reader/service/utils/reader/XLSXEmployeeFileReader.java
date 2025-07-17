package com.vladdumbrava.excel_csv_reader.service.utils.reader;

import com.vladdumbrava.excel_csv_reader.model.Employee;
import com.vladdumbrava.excel_csv_reader.model.Gender;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component("xlsxReader")
@Slf4j
public class XLSXEmployeeFileReader implements EmployeeFileReader {

    @Override
    public List<Employee> read(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            log.info("Opened Excel file for reading: {}", file.getOriginalFilename());

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                log.warn("No sheet found in Excel file.");
                return List.of();
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                log.warn("Excel file has no header row.");
                return List.of();
            }

            int totalRows = sheet.getLastRowNum();
            log.info("Processing Excel sheet with {} data rows.", totalRows);

            List<Employee> employees = IntStream.rangeClosed(1, totalRows)
                    .mapToObj(sheet::getRow)
                    .filter(Objects::nonNull)
                    .map(row -> {
                        try {
                            String name = getCellAsString(row, 0);
                            Integer age = Integer.parseInt(getCellAsString(row, 1));
                            String genderStr = getCellAsString(row, 2);
                            String role = getCellAsString(row, 3);
                            String email = getCellAsString(row, 4);
                            String phone = getCellAsString(row, 5);
                            String activeStr = getCellAsString(row, 6);

                            Boolean active = switch (activeStr.trim().toLowerCase()) {
                                case "true" -> true;
                                case "false" -> false;
                                case "", "n/a", "null" -> null;
                                default -> {
                                    log.warn("Invalid boolean value in row {}: '{}'", row.getRowNum(), activeStr);
                                    yield null;
                                }
                            };

                            Employee employee = new Employee();
                            employee.setName(name);
                            employee.setAge(age);
                            employee.setGender(Gender.valueOf(genderStr.toUpperCase()));
                            employee.setRole(role);
                            employee.setEmail(email);
                            employee.setPhoneNumber(phone);
                            employee.setActive(active);

                            log.info("Parsed employee from row {}: {}", row.getRowNum(), employee);
                            return employee;

                        } catch (Exception e) {
                            log.warn("Failed to parse row {}: {}", row.getRowNum(), e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            log.info("Successfully read {} employee records from Excel file.", employees.size());
            return employees;

        } catch (IOException e) {
            log.error("Error reading Excel file: {}", file.getOriginalFilename(), e);
            return List.of();
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
}
