package com.vladdumbrava.excel_csv_reader.service;

import java.util.List;
import java.util.stream.Collectors;

import com.vladdumbrava.excel_csv_reader.dto.EmployeeDTO;
import com.vladdumbrava.excel_csv_reader.exception.ResourceNotFoundException;
import com.vladdumbrava.excel_csv_reader.model.Employee;
import com.vladdumbrava.excel_csv_reader.repository.EmployeeRepository;
import com.vladdumbrava.excel_csv_reader.service.mapper.EmployeeMapper;
import com.vladdumbrava.excel_csv_reader.service.utils.FileReaderFactory;
import com.vladdumbrava.excel_csv_reader.service.utils.reader.EmployeeFileReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final FileReaderFactory fileReaderFactory;

    public void importEmployees(MultipartFile file) {
        EmployeeFileReader reader = fileReaderFactory.getReader(file);
        log.info("FileReaderFactory chose implementation for reader.");
        List<Employee> employees = reader.read(file);
        employeeRepository.saveAll(employees);
        log.info("Saved employees in repository.");
        log.info("\nProcessed data:\n{}",
                employees.stream()
                        .map(employee -> employeeMapper.entityToDto(employee).toString())
                        .collect(Collectors.joining("\n"))
        );
    }

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = employeeMapper.dtoToEntity(employeeDTO);
        Employee saved = employeeRepository.save(employee);
        log.info("Saved employee in repository.");
        return employeeMapper.entityToDto(saved);
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public EmployeeDTO updateEmployee(Long id, EmployeeDTO newEmployeeDTO) {
        Employee newEmployee = employeeMapper.dtoToEntity(newEmployeeDTO);

        Employee savedEmployee = employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setAge(newEmployee.getAge());
                    employee.setGender(newEmployee.getGender());
                    employee.setRole(newEmployee.getRole());
                    employee.setEmail(newEmployee.getEmail());
                    employee.setPhoneNumber(newEmployee.getPhoneNumber());
                    employee.setActive(newEmployee.getActive());
                    log.info("Updated employee.");
                    return employeeRepository.save(employee);
                })
                .orElseGet(() -> employeeRepository.save(newEmployee));

        return employeeMapper.entityToDto(savedEmployee);
    }

    public EmployeeDTO updateEmployeeName(Long id, String newName) {
        Employee savedEmployee = employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(newName);
                    log.info("Updated employee's name.");
                    return employeeRepository.save(employee);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        return employeeMapper.entityToDto(savedEmployee);
    }
}
