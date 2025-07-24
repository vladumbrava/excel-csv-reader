package com.vladdumbrava.excel_csv_reader.controller;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import com.vladdumbrava.excel_csv_reader.dto.EmployeeDTO;
import com.vladdumbrava.excel_csv_reader.model.Employee;
import com.vladdumbrava.excel_csv_reader.model.Gender;
import com.vladdumbrava.excel_csv_reader.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @Test
    void givenCsv_whenImportEmployees_thenReturnOk() {
        byte[] fileContent = "name,dateOfBirth,gender,role,email,phoneNumber,active\nJohn Doe,1990-01-01,MALE,Engineer,john@example.com,1234567890,true".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "employees.csv",
                "text/csv",
                fileContent
        );

        ResponseEntity<String> response = employeeController.importEmployees(mockFile);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Employees imported successfully.");
        verify(employeeService, times(1)).importEmployees(mockFile);
    }

    @Test
    void givenXlsx_whenImportEmployees_thenReturnOk() throws Exception{
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Employees.xlsx")) {
            assert inputStream != null : "Test file not found in resources";

            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "Employees.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    inputStream
            );

            ResponseEntity<String> response = employeeController.importEmployees(mockFile);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo("Employees imported successfully.");
            verify(employeeService, times(1)).importEmployees(mockFile);
        }
    }

    @Test
    void givenEmployee_whenCreate_thenReturnCreated() {
        Employee employee = new Employee();
        employee.setName("John Doe");
        employee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        employee.setGender(Gender.MALE);
        employee.setRole("Engineer");
        employee.setEmail("john@example.com");
        employee.setPhoneNumber("1234567890");
        employee.setActive(true);

        EmployeeDTO employeeDTO = new EmployeeDTO(
                employee.getName(),
                employee.getDateOfBirth(),
                employee.getGender(),
                employee.getRole(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getActive()
        );

        when(employeeService.createEmployee(employeeDTO)).thenReturn(employeeDTO);

        ResponseEntity<EmployeeDTO> response = employeeController.createEmployee(employeeDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("John Doe");
    }

    @Test
    void givenId_whenDeleteEmployee_thenReturnNoContent() {
        employeeService.createEmployee(new EmployeeDTO("example",
                LocalDate.of(2000,1,1),
                Gender.MALE,
                "example",
                "example@example.com",
                "+4000000007",
                true));

        Long id = 1L;

        ResponseEntity<String> response = employeeController.deleteEmployee(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isEqualTo("Employee deleted successfully");
        verify(employeeService, times(1)).deleteEmployee(id);
    }

    @Test
    void whenGetAllEmployees_thenReturnOk() {
        employeeService.createEmployee(new EmployeeDTO("example",
                LocalDate.of(2000,1,1),
                Gender.MALE,
                "example",
                "example@example.com",
                "+4000000007",
                true));

        employeeService.createEmployee(new EmployeeDTO("example1",
                LocalDate.of(2000,1,1),
                Gender.MALE,
                "example1",
                "example1@example1.com",
                "+4100000007",
                true));

        List<EmployeeDTO> employees = employeeService.getAllEmployees();

        ResponseEntity<List<EmployeeDTO>> response = employeeController.getAllEmployees();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(employees);
    }

    @Test
    void givenIdAndNewEmployee_whenUpdateEmployee_thenReturnOk() {
        Long id = 1L;

        EmployeeDTO updatedDTO = new EmployeeDTO("example1",
                LocalDate.of(2000,1,1),
                Gender.MALE,
                "example1",
                "example1@example1.com",
                "+4100000007",
                true);

        when(employeeService.updateEmployee(eq(id), any(EmployeeDTO.class))).thenReturn(updatedDTO);

        ResponseEntity<EmployeeDTO> response = employeeController.updateEmployee(id, updatedDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("example1");

        verify(employeeService, times(1)).updateEmployee(eq(id), any(EmployeeDTO.class));
    }

    @Test
    void givenIdAndNewName_whenUpdateEmployeeName_thenReturnOk() {
        Long id = 1L;
        String newName = "Updated Name";

        EmployeeDTO updatedEmployee = new EmployeeDTO(
                newName,
                LocalDate.of(1990, 1, 1),
                Gender.MALE,
                "Engineer",
                "john@example.com",
                "+40712345678",
                true
        );

        when(employeeService.updateEmployeeName(id, newName)).thenReturn(updatedEmployee);

        ResponseEntity<EmployeeDTO> response = employeeController.updateEmployeeName(id, newName);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Updated Name");

        verify(employeeService, times(1)).updateEmployeeName(id, newName);
    }
}
