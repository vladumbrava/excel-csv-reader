package com.vladdumbrava.excel_csv_reader.service;

import com.vladdumbrava.excel_csv_reader.dto.EmployeeDTO;
import com.vladdumbrava.excel_csv_reader.model.Employee;
import com.vladdumbrava.excel_csv_reader.repository.EmployeeRepository;
import com.vladdumbrava.excel_csv_reader.service.mapper.EmployeeMapper;
import com.vladdumbrava.excel_csv_reader.service.utils.FileReaderFactory;
import com.vladdumbrava.excel_csv_reader.service.utils.reader.EmployeeFileReader;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final FileReaderFactory fileReaderFactory;

    public void importEmployees(MultipartFile file) {
        EmployeeFileReader reader = fileReaderFactory.getReader(file);
        List<Employee> employees = reader.read(file);
        employeeRepository.saveAll(employees);
        log.info("\nProcessed data:\n{}",
                employees.stream()
                        .map(Employee::toString)
                        .collect(Collectors.joining("\n"))
        );
    }

    public void createEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper)
                .collect(Collectors.toList());
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public void updateEmployee(Long id, Employee newEmployee) {
        employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setAge(newEmployee.getAge());
                    employee.setGender(newEmployee.getGender());
                    employee.setRole(newEmployee.getRole());
                    employee.setEmail(newEmployee.getEmail());
                    employee.setPhoneNumber(newEmployee.getPhoneNumber());
                    employee.setActive(newEmployee.getActive());
                    return employeeRepository.save(employee);
                })
                .orElseGet(() -> employeeRepository.save(newEmployee));
    }

    public void updateEmployeeName(Long id, String newName) {
        employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(newName);
                    return employeeRepository.save(employee);
                })
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
    }
}
