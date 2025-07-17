package com.vladdumbrava.excel_csv_reader.controller;

import com.vladdumbrava.excel_csv_reader.dto.EmployeeDTO;
import com.vladdumbrava.excel_csv_reader.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/import")
    public ResponseEntity<String> importEmployees(@RequestParam("file") MultipartFile file) {
        employeeService.importEmployees(file);
        return ResponseEntity.ok("Employees imported successfully.");
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO savedDto = employeeService.createEmployee(employeeDTO);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-employee/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully.");
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return new ResponseEntity<>(employeeService.getAllEmployees(), HttpStatus.OK);
    }

    @PutMapping("/update-employee/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable("id") Long id, @RequestBody EmployeeDTO newEmployeeDTO) {
        return new ResponseEntity<>(employeeService.updateEmployee(id, newEmployeeDTO), HttpStatus.OK);
    }

    @PatchMapping("/update-employee-name/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployeeName(@PathVariable("id") Long id, @RequestBody String name) {
        return new ResponseEntity<>(employeeService.updateEmployeeName(id, name), HttpStatus.OK);
    }

}
