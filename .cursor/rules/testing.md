---
description: "テストルール: JUnit 5とMockitoを使用した単体テストの書き方、テスト方針、テストカバレッジ"
globs:
  - "**/test/**/*Test.java"
  - "**/test/**/*Tests.java"
alwaysApply: false
---

# テストルール

## テストの基本原則
- **単体テストのみ**: 結合テスト・システムテストは手動テストで実施
- **JUnit 5とMockito**を使用
- **すべてのServiceクラスとControllerクラス**に単体テストを実装
- **正常系・異常系の両方**をテスト
- **テストの独立性**: 各テストは独立して実行可能
- **テストの再現性**: 同じテストは常に同じ結果を返す

## テストクラスの構造

### テストクラスの命名規則
- テストクラス名: `[対象クラス名]Test`（例: `EmployeeServiceTest`）
- パッケージ: `com.company.system.unit`
- `@ExtendWith(MockitoExtension.class)`を使用
- `@DisplayName`でテストクラスの説明を記載

**良い例:**
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService単体テスト")
class EmployeeServiceTest {
    // テストコード
}
```

### テストクラスの構成要素
1. **モックオブジェクトの宣言**: `@Mock`アノテーション
2. **テスト対象のインスタンス化**: `@InjectMocks`アノテーション
3. **テストデータの準備**: `@BeforeEach`でセットアップ
4. **テストメソッド**: `@Test`アノテーション

**良い例:**
```java
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
        // テストデータのセットアップ
        department = new Department(1L, "営業部", "SALES", 
            LocalDateTime.now(), LocalDateTime.now());
        employee = new Employee(/* ... */);
    }
}
```

## テストメソッドの書き方

### テストメソッドの命名規則
- テストメソッド名: `[メソッド名]_[正常系/異常系]_[説明]`
- `@DisplayName`でテストケースIDと説明を記載

**良い例:**
```java
@Test
@DisplayName("UT-001: findAll_正常系 - 全社員を取得")
void findAll_正常系() {
    // テストコード
}

@Test
@DisplayName("UT-003: findById_異常系 - 存在しないIDで社員取得")
void findById_異常系() {
    // テストコード
}
```

**悪い例:**
```java
@Test
void test1() {  // 何をテストしているか不明
    // テストコード
}

@Test
void findAll() {  // 正常系か異常系か不明
    // テストコード
}
```

### Given-When-Thenパターン
すべてのテストメソッドは、Given-When-Thenパターンで記述します。

```java
@Test
@DisplayName("UT-001: findAll_正常系 - 全社員を取得")
void findAll_正常系() {
    // Given: テストの前提条件を準備
    Pageable pageable = PageRequest.of(0, 20);
    Page<Employee> employeePage = new PageImpl<>(List.of(employee), pageable, 1);
    when(employeeRepository.findAllActive(pageable)).thenReturn(employeePage);
    
    // When: テスト対象のメソッドを実行
    Page<EmployeeDto> result = employeeService.findAll(0, 20, null, null);
    
    // Then: 結果を検証
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals("山田太郎", result.getContent().get(0).getName());
    verify(employeeRepository, times(1)).findAllActive(pageable);
}
```

**記載のポイント:**
- **Given**: モックの設定、テストデータの準備
- **When**: テスト対象メソッドの呼び出し
- **Then**: アサーション、モックの検証

## モックの使い方

### モックの設定
- `@Mock`アノテーションでモックオブジェクトを宣言
- `when().thenReturn()`でモックの動作を定義
- `verify()`でメソッドの呼び出しを検証

**良い例:**
```java
@Mock
private EmployeeRepository employeeRepository;

@Test
void findById_正常系() {
    // Given: モックの動作を定義
    when(employeeRepository.findByIdAndNotDeleted(1L))
        .thenReturn(Optional.of(employee));
    
    // When
    EmployeeDto result = employeeService.findById(1L);
    
    // Then: モックの呼び出しを検証
    verify(employeeRepository, times(1)).findByIdAndNotDeleted(1L);
}
```

### モックの検証
- `verify()`: メソッドが呼び出されたことを検証
- `verify(..., times(n))`: 呼び出し回数を検証
- `verify(..., never())`: 呼び出されなかったことを検証

**良い例:**
```java
// 1回呼び出されたことを検証
verify(employeeRepository, times(1)).findById(1L);

// 呼び出されなかったことを検証
verify(employeeRepository, never()).delete(any());

// 引数を検証
verify(employeeRepository).save(argThat(emp -> 
    emp.getName().equals("山田太郎")
));
```

## アサーションの書き方

### 基本的なアサーション
- `assertNotNull()`: nullでないことを検証
- `assertEquals()`: 等しいことを検証
- `assertTrue()`/`assertFalse()`: 真偽値を検証
- `assertThrows()`: 例外が発生することを検証

**良い例:**
```java
// nullチェック
assertNotNull(result);

// 値の等価性チェック
assertEquals(1L, result.getId());
assertEquals("山田太郎", result.getName());

// 真偽値チェック
assertTrue(result.isActive());

// 例外の検証
assertThrows(ResourceNotFoundException.class, () -> {
    employeeService.findById(999L);
});
```

### コレクションのアサーション
- `assertEquals()`: サイズや要素を検証
- `assertTrue()`: 条件を満たす要素が存在することを検証

**良い例:**
```java
// サイズの検証
assertEquals(1, result.getContent().size());

// 要素の検証
assertEquals("山田太郎", result.getContent().get(0).getName());

// 条件を満たす要素が存在することを検証
assertTrue(result.getContent().stream()
    .anyMatch(emp -> emp.getName().equals("山田太郎")));
```

## テストデータの準備

### @BeforeEachでのセットアップ
- 各テストで使用する共通データを`@BeforeEach`で準備
- テストデータは意味のある値を使用

**良い例:**
```java
private Department department;
private Employee employee;

@BeforeEach
void setUp() {
    department = new Department(1L, "営業部", "SALES", 
        LocalDateTime.now(), LocalDateTime.now());
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
```

### テストメソッド内でのデータ準備
- テスト固有のデータはテストメソッド内で準備
- テストデータは明確な値を使用（意味のある名前、日付等）

**良い例:**
```java
@Test
void create_正常系() {
    // Given: テスト固有のデータを準備
    CreateEmployeeRequest request = new CreateEmployeeRequest(
        "佐藤花子",
        "sato@example.com",
        1L,
        LocalDate.of(2024, 1, 1)
    );
    
    // When & Then
    // ...
}
```

## 正常系テストの書き方

### 正常系テストのポイント
- 正常な入力で期待通りの結果が返ることを検証
- すべての出力項目を検証
- モックの呼び出しを検証

**良い例:**
```java
@Test
@DisplayName("UT-001: findAll_正常系 - 全社員を取得")
void findAll_正常系() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<Employee> employeePage = new PageImpl<>(List.of(employee), pageable, 1);
    when(employeeRepository.findAllActive(pageable)).thenReturn(employeePage);
    
    // When
    Page<EmployeeDto> result = employeeService.findAll(0, 20, null, null);
    
    // Then: すべての出力項目を検証
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getTotalPages());
    assertEquals(0, result.getCurrentPage());
    assertEquals("山田太郎", result.getContent().get(0).getName());
    assertEquals("yamada@example.com", result.getContent().get(0).getEmail());
    
    // モックの呼び出しを検証
    verify(employeeRepository, times(1)).findAllActive(pageable);
}
```

## 異常系テストの書き方

### 異常系テストのポイント
- 異常な入力で適切な例外が発生することを検証
- 例外の種類とメッセージを検証
- モックが呼び出されないことを検証（必要に応じて）

**良い例:**
```java
@Test
@DisplayName("UT-003: findById_異常系 - 存在しないIDで社員取得")
void findById_異常系() {
    // Given: 存在しないIDを設定
    when(employeeRepository.findByIdAndNotDeleted(999L))
        .thenReturn(Optional.empty());
    
    // When & Then: 例外が発生することを検証
    assertThrows(ResourceNotFoundException.class, () -> {
        employeeService.findById(999L);
    });
    
    // モックの呼び出しを検証
    verify(employeeRepository, times(1)).findByIdAndNotDeleted(999L);
    verify(employeeRepository, never()).save(any());
}
```

### 例外のメッセージ検証
例外のメッセージも検証する場合は、`assertThrows`の戻り値を使用します。

**良い例:**
```java
@Test
void findById_異常系_メッセージ検証() {
    // Given
    when(employeeRepository.findByIdAndNotDeleted(999L))
        .thenReturn(Optional.empty());
    
    // When & Then
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> employeeService.findById(999L)
    );
    
    assertEquals("社員が見つかりません", exception.getMessage());
}
```

## Controllerテストの書き方

### MockMvcの使用
- `MockMvc`を使用してHTTPリクエストをシミュレート
- `@BeforeEach`で`MockMvc`をセットアップ
- `ObjectMapper`でJSONのシリアライズ/デシリアライズ

**良い例:**
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeController単体テスト")
class EmployeeControllerTest {
    
    @Mock
    private EmployeeService employeeService;
    
    @InjectMocks
    private EmployeeController employeeController;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }
    
    @Test
    @DisplayName("UT-011: getEmployees_正常系 - 社員一覧取得API")
    void getEmployees_正常系() throws Exception {
        // Given
        Page<EmployeeDto> page = new PageImpl<>(List.of(employeeDto), 
            PageRequest.of(0, 20), 1);
        when(employeeService.findAll(0, 20, null, null)).thenReturn(page);
        
        // When & Then
        mockMvc.perform(get("/api/v1/employees"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("山田太郎"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }
}
```

### HTTPステータスコードの検証
- `status().isOk()`: 200 OK
- `status().isCreated()`: 201 Created
- `status().isNoContent()`: 204 No Content
- `status().isNotFound()`: 404 Not Found
- `status().isBadRequest()`: 400 Bad Request

**良い例:**
```java
// 200 OK
mockMvc.perform(get("/api/v1/employees/1"))
    .andExpect(status().isOk());

// 201 Created
mockMvc.perform(post("/api/v1/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
    .andExpect(status().isCreated());

// 404 Not Found
mockMvc.perform(get("/api/v1/employees/999"))
    .andExpect(status().isNotFound());
```

### JSONレスポンスの検証
- `jsonPath()`でJSONの値を検証
- `contentType()`でContent-Typeを検証

**良い例:**
```java
mockMvc.perform(get("/api/v1/employees/1"))
    .andExpect(status().isOk())
    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    .andExpect(jsonPath("$.id").value(1))
    .andExpect(jsonPath("$.name").value("山田太郎"))
    .andExpect(jsonPath("$.email").value("yamada@example.com"))
    .andExpect(jsonPath("$.department.id").value(1))
    .andExpect(jsonPath("$.department.name").value("営業部"));
```

## テストのベストプラクティス

### 1. テストの独立性
- 各テストは独立して実行可能
- テストの実行順序に依存しない
- 他のテストの状態に依存しない

**悪い例:**
```java
private static int counter = 0;  // 状態を共有

@Test
void test1() {
    counter++;  // 他のテストに影響
}

@Test
void test2() {
    assertEquals(1, counter);  // test1に依存
}
```

**良い例:**
```java
@Test
void test1() {
    int localCounter = 0;  // ローカル変数を使用
    localCounter++;
    assertEquals(1, localCounter);
}

@Test
void test2() {
    // test1に依存しない独立したテスト
}
```

### 2. テストデータの管理
- テストデータは明確な値を使用
- 意味のある名前や日付を使用
- テストデータの準備は`@BeforeEach`で実施

### 3. アサーションの明確性
- アサーションのメッセージを明確に
- 期待値と実際の値を明確に区別
- 複数のアサーションは意味のある順序で

**良い例:**
```java
assertEquals("期待される値と実際の値が一致しません", 
    expectedValue, actualValue);
```

### 4. モックの適切な使用
- 必要な依存関係のみをモック
- モックの動作は明確に定義
- モックの呼び出しは適切に検証

### 5. テストカバレッジ
- **単体テストカバレッジ: 80%以上**を目標
- **ビジネスロジック（Service層）: 90%以上**を目標
- カバレッジレポートを確認して、未テストのコードを特定

## テストの禁止事項

### 1. 統合テストの禁止
- データベースにアクセスしない
- 実際のHTTPリクエストを送信しない
- 外部サービスにアクセスしない

**悪い例:**
```java
@SpringBootTest  // 統合テスト
@AutoConfigureMockMvc
class EmployeeServiceTest {
    @Autowired
    private EmployeeRepository employeeRepository;  // 実際のDBにアクセス
}
```

### 2. テスト間の依存関係
- テストの実行順序に依存しない
- グローバルな状態を変更しない

### 3. テストの複雑化
- 1つのテストメソッドで複数のことをテストしない
- テストメソッドは簡潔に

**悪い例:**
```java
@Test
void testMultipleThings() {
    // 複数のことをテスト（悪い例）
    testCreate();
    testUpdate();
    testDelete();
}
```

**良い例:**
```java
@Test
void create_正常系() {
    // 作成のみをテスト
}

@Test
void update_正常系() {
    // 更新のみをテスト
}

@Test
void delete_正常系() {
    // 削除のみをテスト
}
```

## テストコードの例

@src/test/java/com/company/system/unit/EmployeeServiceTest.java
@src/test/java/com/company/system/unit/EmployeeControllerTest.java

## テストカバレッジ
- 単体テストカバレッジ: 80%以上を目標
- ビジネスロジック（Service層）: 90%以上を目標
- カバレッジレポート生成: `mvn test jacoco:report`


