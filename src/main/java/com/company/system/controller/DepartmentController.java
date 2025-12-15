package com.company.system.controller;

import com.company.system.dto.DepartmentDto;
import com.company.system.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部署コントローラー
 */
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 部署一覧を取得
     *
     * @return 部署一覧
     */
    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getDepartments() {
        List<DepartmentDto> departments = departmentService.findAll();
        return ResponseEntity.ok(departments);
    }

    /**
     * IDで部署を取得
     *
     * @param id 部署ID
     * @return 部署
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartment(@PathVariable Long id) {
        DepartmentDto department = departmentService.findById(id);
        return ResponseEntity.ok(department);
    }
}

