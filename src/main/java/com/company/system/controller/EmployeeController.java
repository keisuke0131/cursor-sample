package com.company.system.controller;

import com.company.system.dto.CreateEmployeeRequest;
import com.company.system.dto.EmployeeDto;
import com.company.system.dto.UpdateEmployeeRequest;
import com.company.system.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 社員コントローラー
 */
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * 社員一覧を取得
     *
     * @param page ページ番号（デフォルト: 0）
     * @param size 1ページあたりの件数（デフォルト: 20）
     * @param name 社員名（検索条件、任意）
     * @param departmentId 部署ID（検索条件、任意）
     * @return 社員ページ
     */
    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> getEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long departmentId) {
        Page<EmployeeDto> employees = employeeService.findAll(page, size, name, departmentId);
        return ResponseEntity.ok(employees);
    }

    /**
     * IDで社員を取得
     *
     * @param id 社員ID
     * @return 社員
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Long id) {
        EmployeeDto employee = employeeService.findById(id);
        return ResponseEntity.ok(employee);
    }

    /**
     * 社員を作成
     *
     * @param request 作成リクエスト
     * @return 作成された社員
     */
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeDto employee = employeeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    /**
     * 社員を更新
     *
     * @param id 社員ID
     * @param request 更新リクエスト
     * @return 更新された社員
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        EmployeeDto employee = employeeService.update(id, request);
        return ResponseEntity.ok(employee);
    }

    /**
     * 社員を削除
     *
     * @param id 社員ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


