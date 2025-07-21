package com.vladdumbrava.excel_csv_reader.service.mapper;

import com.vladdumbrava.excel_csv_reader.dto.EmployeeDTO;
import com.vladdumbrava.excel_csv_reader.model.Employee;

import org.springframework.stereotype.Service;

@Service
public class EmployeeMapper {

    public EmployeeDTO entityToDto(Employee employee) {
        return new EmployeeDTO(
                employee.getName(),
                employee.getAge(),
                employee.getGender(),
                employee.getRole(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getActive()
        );
    }

    public Employee dtoToEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setName(employeeDTO.name());
        employee.setAge(employeeDTO.age());
        employee.setGender(employeeDTO.gender());
        employee.setRole(employeeDTO.role());
        employee.setEmail(employeeDTO.email());
        employee.setPhoneNumber(employeeDTO.phoneNumber());
        employee.setActive(employeeDTO.active());
        return employee;
    }
}
