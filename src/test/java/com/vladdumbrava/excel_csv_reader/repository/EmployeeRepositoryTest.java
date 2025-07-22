package com.vladdumbrava.excel_csv_reader.repository;

import java.time.LocalDate;

import com.vladdumbrava.excel_csv_reader.model.Employee;
import com.vladdumbrava.excel_csv_reader.model.Gender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void givenEmployee_WhenSave_ReturnSavedEmployee() {

        //Arrange
        Employee employee = new Employee();
        employee.setName("Example name");
        employee.setDateOfBirth(LocalDate.of(1999,2,6));
        employee.setGender(Gender.MALE);
        employee.setRole("Example role");
        employee.setEmail("example@email.com");
        employee.setPhoneNumber("+40623654789");
        employee.setActive(false);

        //Act
        Employee savedEmployee = employeeRepository.save(employee);

        //Assert
        Assertions.assertThat(savedEmployee).isNotNull();
        Assertions.assertThat(savedEmployee.getId()).isNotNull();

    }
}
