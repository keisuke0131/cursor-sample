---
description: "コーディング規約: 命名規則、コーディングスタイル、コード品質に関するルール"
alwaysApply: true
---

# コーディング規約

## 命名規則

### クラス名
- **PascalCase**を使用
- 名詞を使用
- インターフェースは`I`プレフィックスは付けない（例: `EmployeeRepository`）
- 抽象クラスは`Abstract`プレフィックスを使用（例: `AbstractService`）
- 例外クラスは`Exception`サフィックス（例: `ResourceNotFoundException`）
- DTOは`Dto`または`Request`/`Response`サフィックス（例: `EmployeeDto`, `CreateEmployeeRequest`）

**良い例:**
```java
EmployeeService
EmployeeController
EmployeeRepository
ResourceNotFoundException
CreateEmployeeRequest
```

**悪い例:**
```java
employeeService  // 小文字始まり
IEmployeeRepository  // 不要なIプレフィックス
EmployeeDTO  // 大文字のDTO
```

### メソッド名
- **camelCase**を使用
- 動詞で始める
- 意味が明確になるように命名
- ブール値を返すメソッドは`is`/`has`/`can`で始める

**良い例:**
```java
findById(Long id)
createEmployee(CreateEmployeeRequest request)
updateEmployee(Long id, UpdateEmployeeRequest request)
isValidEmail(String email)
hasPermission(Long userId)
```

**悪い例:**
```java
get(Long id)  // 何を取得するか不明
create(CreateEmployeeRequest request)  // 何を作成するか不明
check()  // 何をチェックするか不明
```

### 変数名
- **camelCase**を使用
- 意味のある名前を使用
- 略語は避ける（`emp`ではなく`employee`）
- ループ変数は`i`, `j`, `k`のみ可
- コレクションは複数形（例: `employees`, `departmentList`）

**良い例:**
```java
Employee employee;
List<Employee> employees;
Long departmentId;
String employeeName;
```

**悪い例:**
```java
Employee e;  // 意味が不明
List<Employee> list;  // 何のリストか不明
Long deptId;  // 略語
String name;  // どのnameか不明
```

### 定数名
- **UPPER_SNAKE_CASE**を使用
- 意味のある名前を使用

**良い例:**
```java
private static final int MAX_RETRY_COUNT = 3;
private static final String DEFAULT_PAGE_SIZE = "20";
private static final String EMPLOYEE_NUMBER_PREFIX = "EMP";
```

**悪い例:**
```java
private static final int maxRetryCount = 3;  // 小文字
private static final int MAX = 3;  // 意味が不明
```

### パッケージ名
- 小文字のみ使用
- ドメイン名を逆順に使用
- 単数形を使用（例: `controller`ではなく`controllers`ではない）

**良い例:**
```java
package com.company.system.controller;
package com.company.system.service;
package com.company.system.repository;
```

## コーディングスタイル

### インデントとフォーマット
- インデント: **スペース4つ**（タブは使用しない）
- 1行の長さ: **120文字以内**
- 中括弧: 開き括弧は行末、閉じ括弧は行頭
- 空行: クラス間は2行、メソッド間は1行

**良い例:**
```java
@Service
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    
    public EmployeeDto findById(Long id) {
        // 実装
    }
    
    public EmployeeDto create(CreateEmployeeRequest request) {
        // 実装
    }
}
```

### JavaDocコメント
- すべての**publicクラス・メソッド**にJavaDocを記述
- `@param`, `@return`, `@throws`を適切に記述
- 説明は簡潔に、日本語で記述

**良い例:**
```java
/**
 * 社員情報を管理するサービスクラス
 */
@Service
public class EmployeeService {
    
    /**
     * IDで社員を取得します
     * 
     * @param id 社員ID
     * @return 社員DTO
     * @throws ResourceNotFoundException 社員が見つからない場合
     */
    public EmployeeDto findById(Long id) {
        // 実装
    }
}
```

**悪い例:**
```java
// JavaDocなし
public class EmployeeService {
    
    // パラメータや戻り値の説明なし
    public EmployeeDto findById(Long id) {
        // 実装
    }
}
```

### インラインコメント
- 複雑なロジックには説明コメントを追加
- 自明なコードにはコメントを付けない
- TODOコメントには担当者と期限を記載

**良い例:**
```java
// 社員番号の自動採番（形式: EMP001, EMP002, ...）
String employeeNumber = generateEmployeeNumber();

// TODO: パフォーマンス改善が必要（担当者: 山田、期限: 2025-02-01）
List<Employee> employees = employeeRepository.findAll();
```

**悪い例:**
```java
// 変数に値を代入
String name = request.getName();  // 自明なコードにコメント

// 社員を取得
Employee employee = employeeRepository.findById(id);  // 自明
```

### インポート文
- ワイルドカードインポート（`import java.util.*;`）は使用しない
- 未使用のインポートは削除
- 静的インポートは適切に使用

**良い例:**
```java
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
```

**悪い例:**
```java
import java.util.*;  // ワイルドカード
import java.util.ArrayList;  // 未使用
```

## コード品質

### 禁止事項
- **魔法数字**: 数値リテラルは定数として定義
- **深いネスト**: 3階層以上のネストは避ける
- **長いメソッド**: 1メソッドは50行以内を目標
- **巨大なクラス**: 1クラスは300行以内を目標
- **重複コード**: DRY原則に従う

**悪い例:**
```java
// 魔法数字
if (retryCount > 3) {  // 3は何を意味するか不明
    // 処理
}

// 深いネスト
if (condition1) {
    if (condition2) {
        if (condition3) {
            if (condition4) {  // 4階層
                // 処理
            }
        }
    }
}
```

**良い例:**
```java
// 定数として定義
private static final int MAX_RETRY_COUNT = 3;
if (retryCount > MAX_RETRY_COUNT) {
    // 処理
}

// 早期リターンでネストを浅く
if (!condition1) {
    return;
}
if (!condition2) {
    return;
}
// 処理
```

### 推奨事項
- **早期リターン**: ガード句を使用してネストを浅く
- **Optionalの適切な使用**: nullチェックにOptionalを使用
- **Stream APIの活用**: コレクション操作にStream APIを使用
- **不変オブジェクト**: 可能な限り不変オブジェクトを使用

**良い例:**
```java
// 早期リターン
public EmployeeDto findById(Long id) {
    if (id == null) {
        throw new IllegalArgumentException("IDは必須です");
    }
    // 処理
}

// Optionalの使用
Optional<Employee> employee = employeeRepository.findById(id);
return employee.map(this::convertToDto)
    .orElseThrow(() -> new ResourceNotFoundException("社員が見つかりません"));

// Stream API
List<String> names = employees.stream()
    .map(Employee::getName)
    .filter(name -> name.startsWith("山"))
    .collect(Collectors.toList());
```

## コードレビューの観点
1. **命名**: 意味が明確か、規約に従っているか
2. **可読性**: コードが読みやすいか、理解しやすいか
3. **保守性**: 将来の変更に対応しやすいか
4. **パフォーマンス**: 非効率な処理がないか
5. **セキュリティ**: セキュリティホールがないか
6. **テスト**: テスト可能な設計か


