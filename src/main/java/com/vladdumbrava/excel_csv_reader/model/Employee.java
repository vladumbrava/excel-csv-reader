package com.vladdumbrava.excel_csv_reader.model;

import java.time.LocalDate;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Table(name = "employees")
@Data
public class Employee {

    @Id
    @Column(name = "employee_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "role")
    private String role;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "active")
    private Boolean active;
}
