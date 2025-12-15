package com.company.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 社員DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String employeeNumber;
    private String name;
    private String email;
    private DepartmentDto department;
    private LocalDate joinDate;
    private LocalDateTime createdAt;
}

