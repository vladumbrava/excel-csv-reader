package com.vladdumbrava.excel_csv_reader.service.utils.reader;

import com.vladdumbrava.excel_csv_reader.model.Employee;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeFileReader {
    List<Employee> read(MultipartFile file);
}
