package com.company.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 部署DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String code;
}


