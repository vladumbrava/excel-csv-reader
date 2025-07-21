package com.vladdumbrava.excel_csv_reader.service.utils.reader;

import java.util.List;

import com.vladdumbrava.excel_csv_reader.model.Employee;

import org.springframework.web.multipart.MultipartFile;

public interface EmployeeFileReader {
    List<Employee> read(MultipartFile file);
}
