package com.vladdumbrava.excel_csv_reader.dto;

import com.vladdumbrava.excel_csv_reader.model.Gender;

public record EmployeeDTO(
        long id,
        String name,
        Integer age,
        Gender gender,
        String role,
        String email,
        String phoneNumber,
        Boolean active
) {
}
