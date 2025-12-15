package com.company.system.service;

import com.company.system.dto.CreateEmployeeRequest;
import com.company.system.dto.DepartmentDto;
import com.company.system.dto.EmployeeDto;
import com.company.system.dto.UpdateEmployeeRequest;
import com.company.system.exception.DuplicateResourceException;
import com.company.system.exception.ResourceNotFoundException;
import com.company.system.model.Department;
import com.company.system.model.Employee;
import com.company.system.repository.DepartmentRepository;
import com.company.system.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 社員サービス
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    /**
     * 社員一覧を取得（ページネーション対応）
     *
     * @param page ページ番号
     * @param size 1ページあたりの件数
     * @param name 社員名（検索条件、任意）
     * @param departmentId 部署ID（検索条件、任意）
     * @return 社員ページ
     */
    @Transactional(readOnly = true)
    public Page<EmployeeDto> findAll(int page, int size, String name, Long departmentId) {
        log.info("社員一覧取得開始: page={}, size={}, name={}, departmentId={}", page, size, name, departmentId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employees;
        
        if (name != null || departmentId != null) {
            employees = employeeRepository.findByConditions(name, departmentId, pageable);
        } else {
            employees = employeeRepository.findAllActive(pageable);
        }
        
        Page<EmployeeDto> result = employees.map(this::convertToDto);
        log.info("社員一覧取得完了: 総件数={}", result.getTotalElements());
        return result;
    }

    /**
     * IDで社員を取得
     *
     * @param id 社員ID
     * @return 社員DTO
     */
    @Transactional(readOnly = true)
    public EmployeeDto findById(Long id) {
        log.info("社員取得開始: id={}", id);
        Employee employee = employeeRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("社員が見つかりません"));
        EmployeeDto result = convertToDto(employee);
        log.info("社員取得完了: id={}", id);
        return result;
    }

    /**
     * 社員を作成
     *
     * @param request 作成リクエスト
     * @return 作成された社員DTO
     */
    public EmployeeDto create(CreateEmployeeRequest request) {
        log.info("社員作成開始: name={}, email={}", request.getName(), request.getEmail());
        
        // メールアドレスの重複チェック
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("メールアドレスが重複しています");
        }
        
        // 部署の存在チェック
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("部署が存在しません"));
        
        // 社員番号の自動採番
        String employeeNumber = generateEmployeeNumber();
        
        // 社員エンティティの作成
        Employee employee = new Employee();
        employee.setEmployeeNumber(employeeNumber);
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(department);
        employee.setJoinDate(request.getJoinDate());
        
        Employee savedEmployee = employeeRepository.save(employee);
        EmployeeDto result = convertToDto(savedEmployee);
        log.info("社員作成完了: id={}, employeeNumber={}", result.getId(), result.getEmployeeNumber());
        return result;
    }

    /**
     * 社員を更新
     *
     * @param id 社員ID
     * @param request 更新リクエスト
     * @return 更新された社員DTO
     */
    public EmployeeDto update(Long id, UpdateEmployeeRequest request) {
        log.info("社員更新開始: id={}", id);
        
        Employee employee = employeeRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("社員が見つかりません"));
        
        // メールアドレスの変更がある場合、重複チェック
        if (request.getEmail() != null && !request.getEmail().equals(employee.getEmail())) {
            if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new DuplicateResourceException("メールアドレスが重複しています");
            }
            employee.setEmail(request.getEmail());
        }
        
        // 名前の更新
        if (request.getName() != null) {
            employee.setName(request.getName());
        }
        
        // 部署の更新
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("部署が存在しません"));
            employee.setDepartment(department);
        }
        
        Employee updatedEmployee = employeeRepository.save(employee);
        EmployeeDto result = convertToDto(updatedEmployee);
        log.info("社員更新完了: id={}", id);
        return result;
    }

    /**
     * 社員を削除（論理削除）
     *
     * @param id 社員ID
     */
    public void delete(Long id) {
        log.info("社員削除開始: id={}", id);
        
        Employee employee = employeeRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("社員が見つかりません"));
        
        employee.setDeletedAt(LocalDateTime.now());
        employeeRepository.save(employee);
        log.info("社員削除完了: id={}", id);
    }

    /**
     * 社員番号を自動採番
     *
     * @return 社員番号
     */
    private String generateEmployeeNumber() {
        // 既存の社員番号の最大値を取得（削除されていないもののみ）
        long count = employeeRepository.findAllActive(PageRequest.of(0, 1)).getTotalElements();
        
        // 既存の社員番号から最大値を取得
        int maxNumber = 0;
        if (count > 0) {
            // 全社員を取得して最大の社員番号を探す（簡易実装）
            // 本番環境では、シーケンスや専用のテーブルを使用することを推奨
            List<Employee> allEmployees = employeeRepository.findAll();
            for (Employee emp : allEmployees) {
                if (emp.getDeletedAt() == null && emp.getEmployeeNumber() != null) {
                    try {
                        String numStr = emp.getEmployeeNumber().replace("EMP", "");
                        int num = Integer.parseInt(numStr);
                        if (num > maxNumber) {
                            maxNumber = num;
                        }
                    } catch (NumberFormatException e) {
                        // 無効な形式の場合はスキップ
                    }
                }
            }
        }
        
        int nextNumber = maxNumber + 1;
        return String.format("EMP%03d", nextNumber);
    }

    /**
     * エンティティをDTOに変換
     *
     * @param employee 社員エンティティ
     * @return 社員DTO
     */
    private EmployeeDto convertToDto(Employee employee) {
        DepartmentDto departmentDto = new DepartmentDto(
                employee.getDepartment().getId(),
                employee.getDepartment().getName(),
                employee.getDepartment().getCode()
        );
        
        return new EmployeeDto(
                employee.getId(),
                employee.getEmployeeNumber(),
                employee.getName(),
                employee.getEmail(),
                departmentDto,
                employee.getJoinDate(),
                employee.getCreatedAt()
        );
    }
}

