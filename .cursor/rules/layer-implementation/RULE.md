---
description: "レイヤー別実装ルール: Controller、Service、Repository、Model、DTO層の実装ガイドライン"
globs:
  - "**/controller/**/*.java"
  - "**/service/**/*.java"
  - "**/repository/**/*.java"
  - "**/model/**/*.java"
  - "**/dto/**/*.java"
alwaysApply: false
---

# レイヤー別実装ルール

## Controller層

### 実装ルール
- `@RestController`と`@RequestMapping`を使用
- `@Valid`でリクエストボディをバリデーション
- 適切なHTTPステータスコードを返却（200 OK, 201 Created, 204 No Content, 404 Not Found等）
- ビジネスロジックはService層に委譲
- DTOを使用してエンティティを直接返却しない

### 実装例
@src/main/java/com/company/system/controller/EmployeeController.java

### 良い例
```java
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Long id) {
        EmployeeDto employee = employeeService.findById(id);
        return ResponseEntity.ok(employee);
    }
    
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeDto createdUser = employeeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
```

### 悪い例
```java
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    
    @Autowired
    private EmployeeRepository employeeRepository;  // Repositoryを直接使用
    
    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable Long id) {  // エンティティを直接返却
        return employeeRepository.findById(id).orElse(null);
    }
    
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        // ビジネスロジックをControllerに実装（悪い例）
        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Employee saved = employeeRepository.save(employee);
        return ResponseEntity.ok(saved);
    }
}
```

## Service層

### 実装ルール
- `@Service`アノテーションを使用
- `@Transactional`でトランザクション管理
- 読み取り専用メソッドには`@Transactional(readOnly = true)`を使用
- ビジネスルールのチェックを実装
- 例外を適切にスロー（ResourceNotFoundException, DuplicateResourceException等）
- ログ出力を適切に実装（`@Slf4j`を使用）

### 実装例
@src/main/java/com/company/system/service/EmployeeService.java

### 良い例
```java
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    
    @Transactional(readOnly = true)
    public EmployeeDto findById(Long id) {
        log.info("社員取得開始: id={}", id);
        Employee employee = employeeRepository.findByIdAndNotDeleted(id)
            .orElseThrow(() -> new ResourceNotFoundException("社員が見つかりません"));
        EmployeeDto result = convertToDto(employee);
        log.info("社員取得完了: id={}", id);
        return result;
    }
    
    public EmployeeDto create(CreateEmployeeRequest request) {
        log.info("社員作成開始: name={}, email={}", request.getName(), request.getEmail());
        
        // メールアドレスの重複チェック
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("メールアドレスが重複しています");
        }
        
        // 部署の存在チェック
        Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("部署が存在しません"));
        
        // 社員作成処理
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(department);
        Employee savedEmployee = employeeRepository.save(employee);
        
        return convertToDto(savedEmployee);
    }
}
```

### 悪い例
```java
@Service
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    // @Transactionalがない
    public EmployeeDto findById(Long id) {
        // ログ出力がない
        Employee employee = employeeRepository.findById(id)
            .orElse(null);  // nullを返す（例外をスローすべき）
        return convertToDto(employee);
    }
    
    // ビジネスルールのチェックがない
    public EmployeeDto create(CreateEmployeeRequest request) {
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        Employee saved = employeeRepository.save(employee);
        return convertToDto(saved);
    }
}
```

## Repository層

### 実装ルール
- `JpaRepository`を継承
- メソッド名でクエリを自動生成
- 複雑なクエリは`@Query`で明示的に定義
- 論理削除の場合は`deletedAt IS NULL`をチェック

### 実装例
@src/main/java/com/company/system/repository/EmployeeRepository.java

### 良い例
```java
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
}
```

### 悪い例
```java
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    // 論理削除のチェックがない
    Optional<Employee> findByEmail(String email);
    
    // 削除された社員も取得してしまう
    Optional<Employee> findById(Long id);
}
```

## Model層（Entity）

### 実装ルール
- `@Entity`と`@Table`を使用
- `@PrePersist`で作成日時の自動設定
- `@PreUpdate`で更新日時の自動更新
- 論理削除用の`deletedAt`フィールドを用意

### 実装例
@src/main/java/com/company/system/model/Employee.java

### 良い例
```java
@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "employee_number", nullable = false, unique = true, length = 20)
    private String employeeNumber;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    
    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### 悪い例
```java
@Entity
@Table(name = "employees")
public class Employee {
    
    @Id
    private Long id;  // @GeneratedValueがない
    
    private String name;  // @Columnがない、制約が不明
    
    // 作成日時・更新日時の自動設定がない
    // 論理削除用のフィールドがない
}
```

## DTO層

### 実装ルール
- エンティティを直接返却しない
- 必要な情報のみを含める
- リクエスト用とレスポンス用を分離
- バリデーションアノテーションを使用

### 実装例
@src/main/java/com/company/system/dto/CreateEmployeeRequest.java

### 良い例
```java
/**
 * 社員作成リクエストDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeRequest {
    
    @NotBlank(message = "名前は必須です")
    @Size(max = 100, message = "名前は100文字以内で入力してください")
    private String name;
    
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "メールアドレスの形式が正しくありません")
    private String email;
    
    @NotNull(message = "部署IDは必須です")
    private Long departmentId;
    
    @NotNull(message = "入社日は必須です")
    private LocalDate joinDate;
}
```

### 悪い例
```java
// バリデーションアノテーションがない
public class CreateEmployeeRequest {
    private String name;
    private String email;
    private Long departmentId;
}

// エンティティを直接返却（悪い例）
@GetMapping("/{id}")
public Employee getEmployee(@PathVariable Long id) {
    return employeeRepository.findById(id).orElse(null);
}
```

