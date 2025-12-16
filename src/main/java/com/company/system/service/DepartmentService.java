package com.company.system.service;

import com.company.system.dto.DepartmentDto;
import com.company.system.model.Department;
import com.company.system.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 部署サービス
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    /**
     * 部署一覧を取得
     *
     * @return 部署一覧
     */
    @Transactional(readOnly = true)
    public List<DepartmentDto> findAll() {
        log.info("部署一覧取得開始");
        List<Department> departments = departmentRepository.findAll();
        List<DepartmentDto> result = departments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        log.info("部署一覧取得完了: 件数={}", result.size());
        return result;
    }

    /**
     * IDで部署を取得
     *
     * @param id 部署ID
     * @return 部署DTO
     */
    @Transactional(readOnly = true)
    public DepartmentDto findById(Long id) {
        log.info("部署取得開始: id={}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new com.company.system.exception.ResourceNotFoundException("部署が見つかりません"));
        DepartmentDto result = convertToDto(department);
        log.info("部署取得完了: id={}", id);
        return result;
    }

    /**
     * エンティティをDTOに変換
     *
     * @param department 部署エンティティ
     * @return 部署DTO
     */
    private DepartmentDto convertToDto(Department department) {
        return new DepartmentDto(
                department.getId(),
                department.getName(),
                department.getCode()
        );
    }
}


