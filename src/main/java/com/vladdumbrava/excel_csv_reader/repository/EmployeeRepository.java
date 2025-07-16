package com.vladdumbrava.excel_csv_reader.repository;

import com.vladdumbrava.excel_csv_reader.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
