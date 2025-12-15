package com.company.system.repository;

import com.company.system.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 部署リポジトリ
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * 部署コードで部署を検索
     *
     * @param code 部署コード
     * @return 部署
     */
    Optional<Department> findByCode(String code);

    /**
     * 部署名で部署を検索
     *
     * @param name 部署名
     * @return 部署
     */
    Optional<Department> findByName(String name);
}

