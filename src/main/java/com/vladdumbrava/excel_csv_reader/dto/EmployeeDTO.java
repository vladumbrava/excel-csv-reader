package com.vladdumbrava.excel_csv_reader.dto;

import java.time.LocalDate;

import com.vladdumbrava.excel_csv_reader.model.Gender;

public record EmployeeDTO(
        String name,
        LocalDate dateOfBirth,
        Gender gender,
        String role,
        String email,
        String phoneNumber,
        Boolean active
) {
}
