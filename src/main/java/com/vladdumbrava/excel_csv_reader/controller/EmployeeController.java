package com.vladdumbrava.excel_csv_reader.controller;

import com.vladdumbrava.excel_csv_reader.dto.EmployeeDTO;
import com.vladdumbrava.excel_csv_reader.model.Employee;
import com.vladdumbrava.excel_csv_reader.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public void createEmployee(@RequestBody Employee employee) {
        employeeService.createEmployee(employee);
    }

    @DeleteMapping("/delete-employee/{id}")
    public void deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @PutMapping("/update-employee/{id}")
    public void updateEmployee(@PathVariable("id") Long id, @RequestBody Employee newEmployee) {
        employeeService.updateEmployee(id, newEmployee);
    }

    @PatchMapping("/update-employee-name/{id}")
    public void updateEmployeeName(@PathVariable("id") Long id, @RequestBody String name) {
        employeeService.updateEmployeeName(id, name);
    }

}
