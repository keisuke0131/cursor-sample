package com.company.system.unit;

import com.company.system.dto.CreateEmployeeRequest;
import com.company.system.dto.EmployeeDto;
import com.company.system.exception.DuplicateResourceException;
import com.company.system.exception.ResourceNotFoundException;
import com.company.system.model.Department;
import com.company.system.model.Employee;
import com.company.system.repository.DepartmentRepository;
import com.company.system.repository.EmployeeRepository;
import com.company.system.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * EmployeeServiceの単体テスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService単体テスト")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Department department;
    private Employee employee;

    @BeforeEach
    void setUp() {
        department = new Department(1L, "営業部", "SALES", LocalDateTime.now(), LocalDateTime.now());
        employee = new Employee(
                1L,
                "EMP001",
                "山田太郎",
                "yamada@example.com",
                department,
                LocalDate.of(2024, 1, 1),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("UT-001: findAll_正常系 - 全社員を取得")
    void findAll_正常系() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Employee> employeePage = new PageImpl<>(List.of(employee), pageable, 1);
        when(employeeRepository.findAllActive(pageable)).thenReturn(employeePage);

        // When
        Page<EmployeeDto> result = employeeService.findAll(0, 20, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("山田太郎", result.getContent().get(0).getName());
        verify(employeeRepository, times(1)).findAllActive(pageable);
    }

    @Test
    @DisplayName("UT-002: findById_正常系 - 存在するIDで社員取得")
    void findById_正常系() {
        // Given
        when(employeeRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(employee));

        // When
        EmployeeDto result = employeeService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("山田太郎", result.getName());
        assertEquals("yamada@example.com", result.getEmail());
        verify(employeeRepository, times(1)).findByIdAndNotDeleted(1L);
    }

    @Test
    @DisplayName("UT-003: findById_異常系 - 存在しないIDで社員取得")
    void findById_異常系() {
        // Given
        when(employeeRepository.findByIdAndNotDeleted(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.findById(999L);
        });
        verify(employeeRepository, times(1)).findByIdAndNotDeleted(999L);
    }

    @Test
    @DisplayName("UT-004: create_正常系 - 新しい社員を作成")
    void create_正常系() {
        // Given
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "佐藤花子",
                "sato@example.com",
                1L,
                LocalDate.of(2024, 1, 1)
        );
        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.findAll()).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // When
        EmployeeDto result = employeeService.create(request);

        // Then
        assertNotNull(result);
        verify(employeeRepository, times(1)).findByEmail(request.getEmail());
        verify(departmentRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("UT-005: create_異常系_メール重複 - 重複するメールアドレスで作成")
    void create_異常系_メール重複() {
        // Given
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "佐藤花子",
                "yamada@example.com",
                1L,
                LocalDate.of(2024, 1, 1)
        );
        when(employeeRepository.findByEmail("yamada@example.com")).thenReturn(Optional.of(employee));

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            employeeService.create(request);
        });
        verify(employeeRepository, times(1)).findByEmail("yamada@example.com");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("UT-006: create_異常系_部署不存在 - 存在しない部署IDで作成")
    void create_異常系_部署不存在() {
        // Given
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "佐藤花子",
                "sato@example.com",
                999L,
                LocalDate.of(2024, 1, 1)
        );
        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.create(request);
        });
        verify(departmentRepository, times(1)).findById(999L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("UT-009: delete_正常系 - 既存社員を削除")
    void delete_正常系() {
        // Given
        when(employeeRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // When
        employeeService.delete(1L);

        // Then
        verify(employeeRepository, times(1)).findByIdAndNotDeleted(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
        assertNotNull(employee.getDeletedAt());
    }

    @Test
    @DisplayName("UT-010: delete_異常系_存在しないID - 存在しないIDで削除")
    void delete_異常系_存在しないID() {
        // Given
        when(employeeRepository.findByIdAndNotDeleted(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.delete(999L);
        });
        verify(employeeRepository, times(1)).findByIdAndNotDeleted(999L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }
}

