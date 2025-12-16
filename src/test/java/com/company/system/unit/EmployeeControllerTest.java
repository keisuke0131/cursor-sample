package com.company.system.unit;

import com.company.system.controller.EmployeeController;
import com.company.system.dto.CreateEmployeeRequest;
import com.company.system.dto.EmployeeDto;
import com.company.system.dto.DepartmentDto;
import com.company.system.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * EmployeeControllerの単体テスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeController単体テスト")
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        DepartmentDto departmentDto = new DepartmentDto(1L, "営業部", "SALES");
        employeeDto = new EmployeeDto(
                1L,
                "EMP001",
                "山田太郎",
                "yamada@example.com",
                departmentDto,
                LocalDate.of(2024, 1, 1),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("UT-011: getEmployees_正常系 - 社員一覧取得API")
    void getEmployees_正常系() throws Exception {
        // Given
        Page<EmployeeDto> page = new PageImpl<>(List.of(employeeDto), PageRequest.of(0, 20), 1);
        when(employeeService.findAll(0, 20, null, null)).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("山田太郎"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("UT-012: getEmployee_正常系 - 社員詳細取得API")
    void getEmployee_正常系() throws Exception {
        // Given
        when(employeeService.findById(1L)).thenReturn(employeeDto);

        // When & Then
        mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("山田太郎"))
                .andExpect(jsonPath("$.email").value("yamada@example.com"));
    }

    @Test
    @DisplayName("UT-014: createEmployee_正常系 - 社員作成API")
    void createEmployee_正常系() throws Exception {
        // Given
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "佐藤花子",
                "sato@example.com",
                1L,
                LocalDate.of(2024, 1, 1)
        );
        EmployeeDto createdDto = new EmployeeDto(
                2L,
                "EMP002",
                "佐藤花子",
                "sato@example.com",
                new DepartmentDto(1L, "営業部", "SALES"),
                LocalDate.of(2024, 1, 1),
                LocalDateTime.now()
        );
        when(employeeService.create(any(CreateEmployeeRequest.class))).thenReturn(createdDto);

        // When & Then
        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("佐藤花子"))
                .andExpect(jsonPath("$.employeeNumber").value("EMP002"));
    }

    @Test
    @DisplayName("UT-015: createEmployee_異常系_バリデーション - 無効なリクエスト")
    void createEmployee_異常系_バリデーション() throws Exception {
        // Given
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "", // 空文字（バリデーションエラー）
                "invalid-email", // 無効なメール形式
                null, // null（バリデーションエラー）
                null // null（バリデーションエラー）
        );

        // When & Then
        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}


