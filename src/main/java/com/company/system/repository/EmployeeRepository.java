package com.company.system.repository;

import com.company.system.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 社員リポジトリ
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * メールアドレスで社員を検索（削除されていないもののみ）
     *
     * @param email メールアドレス
     * @return 社員
     */
    @Query("SELECT e FROM Employee e WHERE e.email = :email AND e.deletedAt IS NULL")
    Optional<Employee> findByEmail(@Param("email") String email);

    /**
     * 社員番号で社員を検索（削除されていないもののみ）
     *
     * @param employeeNumber 社員番号
     * @return 社員
     */
    @Query("SELECT e FROM Employee e WHERE e.employeeNumber = :employeeNumber AND e.deletedAt IS NULL")
    Optional<Employee> findByEmployeeNumber(@Param("employeeNumber") String employeeNumber);

    /**
     * IDで社員を検索（削除されていないもののみ）
     *
     * @param id 社員ID
     * @return 社員
     */
    @Query("SELECT e FROM Employee e WHERE e.id = :id AND e.deletedAt IS NULL")
    Optional<Employee> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * 条件で社員を検索（削除されていないもののみ）
     *
     * @param name 社員名（部分一致、null可）
     * @param departmentId 部署ID（null可）
     * @param pageable ページネーション情報
     * @return 社員ページ
     */
    @Query("SELECT e FROM Employee e WHERE e.deletedAt IS NULL " +
           "AND (:name IS NULL OR e.name LIKE %:name%) " +
           "AND (:departmentId IS NULL OR e.department.id = :departmentId)")
    Page<Employee> findByConditions(@Param("name") String name,
                                     @Param("departmentId") Long departmentId,
                                     Pageable pageable);

    /**
     * 削除されていない社員を全件取得
     *
     * @param pageable ページネーション情報
     * @return 社員ページ
     */
    @Query("SELECT e FROM Employee e WHERE e.deletedAt IS NULL")
    Page<Employee> findAllActive(Pageable pageable);
}

