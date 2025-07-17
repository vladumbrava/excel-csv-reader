package com.vladdumbrava.excel_csv_reader.service.mapper;

import com.vladdumbrava.excel_csv_reader.dto.EmployeeDTO;
import com.vladdumbrava.excel_csv_reader.model.Employee;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class EmployeeMapper implements Function<Employee, EmployeeDTO>{

    @Override
    public EmployeeDTO apply(Employee employee) {
        return new EmployeeDTO(
                employee.getId(),
                employee.getName(),
                employee.getAge(),
                employee.getGender(),
                employee.getRole(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getActive()
        );
    }
}
