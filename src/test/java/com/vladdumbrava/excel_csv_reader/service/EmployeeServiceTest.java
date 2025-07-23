package com.vladdumbrava.excel_csv_reader.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.vladdumbrava.excel_csv_reader.dto.EmployeeDTO;
import com.vladdumbrava.excel_csv_reader.exception.ResourceNotFoundException;
import com.vladdumbrava.excel_csv_reader.model.Employee;
import com.vladdumbrava.excel_csv_reader.model.Gender;
import com.vladdumbrava.excel_csv_reader.repository.EmployeeRepository;
import com.vladdumbrava.excel_csv_reader.service.mapper.EmployeeMapper;
import com.vladdumbrava.excel_csv_reader.service.utils.FileReaderFactory;
import com.vladdumbrava.excel_csv_reader.service.utils.reader.EmployeeFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private FileReaderFactory fileReaderFactory;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setName("John Doe");
        employee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        employee.setGender(Gender.MALE);
        employee.setRole("Engineer");
        employee.setEmail("john@example.com");
        employee.setPhoneNumber("1234567890");
        employee.setActive(true);

        employeeDTO = new EmployeeDTO(
                employee.getName(),
                employee.getDateOfBirth(),
                employee.getGender(),
                employee.getRole(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getActive()
        );
    }

    @Test
    public void givenEmployee_whenCreate_thenReturnEmployee() {
        when(employeeMapper.dtoToEntity(employeeDTO)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.entityToDto(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.createEmployee(employeeDTO);

        assertThat(result).isNotNull();
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    public void whenGetAll_thenReturnEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(employeeMapper.entityToDto(employee)).thenReturn(employeeDTO);

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().email()).isEqualTo("john@example.com");
    }

    @Test
    public void givenEmployeeId_whenDelete_thenDelete() {
        Long id = employee.getId();
        employeeService.deleteEmployee(id);
        verify(employeeRepository, times(1)).deleteById(id);
    }

    @Test
    void givenExistingEmployee_whenUpdate_thenSaveUpdated() {
        Long id = employee.getId();

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeMapper.dtoToEntity(employeeDTO)).thenReturn(employee);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.entityToDto(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.updateEmployee(id, employeeDTO);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("John Doe");
    }

    @Test
    void givenMissingEmployee_whenUpdate_thenThrowException() {
        Long id = 99L;

        when(employeeRepository.findById(id)).thenReturn(Optional.empty());
        when(employeeMapper.dtoToEntity(employeeDTO)).thenReturn(employee);

        assertThatThrownBy(() -> employeeService.updateEmployee(id, employeeDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found with id: " + id);

        verify(employeeRepository, never()).save(any());
        verify(employeeMapper).dtoToEntity(employeeDTO);
        verify(employeeMapper, never()).entityToDto(any());
    }
    @Test
    void givenExistingEmployee_whenUpdateName_thenSaveUpdated() {
        Long id = employee.getId();

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.entityToDto(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.updateEmployeeName(id, "Jane Doe");

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("John Doe"); // because we mocked entityToDto(employee)
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void givenMissingEmployee_whenUpdateName_thenThrowException() {
        Long id = employee.getId();

        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployeeName(id, "Jane Doe"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: " + id);
    }

    @Test
    void givenFileAndReader_whenImport_thenSaveParsedEmployees() {
        MultipartFile mockFile = mock(MultipartFile.class);
        EmployeeFileReader reader = mock(EmployeeFileReader.class);

        when(fileReaderFactory.getReader(mockFile)).thenReturn(reader);
        when(reader.read(mockFile)).thenReturn(List.of(employee));

        when(employeeMapper.entityToDto(employee)).thenReturn(employeeDTO);

        employeeService.importEmployees(mockFile);

        verify(fileReaderFactory).getReader(mockFile);
        verify(employeeRepository).saveAll(anyList());
        verify(employeeMapper).entityToDto(employee);
    }
}
