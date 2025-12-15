# API仕様書：社員管理システム

## 1. ドキュメント情報

| 項目 | 内容 |
|------|------|
| ドキュメント名 | API仕様書 |
| システム名 | 社員管理システム |
| バージョン | 1.0 |
| 作成日 | 2025-12-01 |
| 作成者 | [作成者名] |

---

## 2. 概要

本ドキュメントでは、社員管理システムのREST APIの詳細仕様を定義します。

### 2.1 API基本情報
- ベースURL: `http://localhost:8080/api/v1`
- 認証方式: なし（現バージョン）
- データ形式: JSON
- 文字コード: UTF-8

### 2.2 共通レスポンス形式

#### 成功レスポンス
- ステータスコード: 200 OK, 201 Created, 204 No Content
- レスポンスボディ: JSON形式

#### エラーレスポンス
```json
{
  "errorCode": "ERR001",
  "message": "エラーメッセージ",
  "timestamp": "2025-12-01T00:00:00"
}
```

---

## 3. API一覧

| メソッド | パス | 機能 | 認証 |
|---------|------|------|------|
| GET | /employees | 社員一覧取得 | 不要 |
| GET | /employees/{id} | 社員詳細取得 | 不要 |
| POST | /employees | 社員作成 | 不要 |
| PUT | /employees/{id} | 社員更新 | 不要 |
| DELETE | /employees/{id} | 社員削除 | 不要 |
| GET | /departments | 部署一覧取得 | 不要 |
| GET | /departments/{id} | 部署詳細取得 | 不要 |

---

## 4. API詳細仕様

### 4.1 GET /api/v1/employees

#### 概要
社員一覧を取得します。

#### リクエスト
**パスパラメータ**
なし

**クエリパラメータ**
| パラメータ名 | 型 | 必須 | デフォルト値 | 説明 |
|------------|-----|------|------------|------|
| page | Integer | 任意 | 0 | ページ番号（0始まり） |
| size | Integer | 任意 | 20 | 1ページあたりの件数 |
| name | String | 任意 | - | 社員名（部分一致検索） |
| departmentId | Long | 任意 | - | 部署ID |

**リクエスト例**
```
GET /api/v1/employees?page=0&size=20&name=山田&departmentId=1
```

#### レスポンス
**成功時（200 OK）**
```json
{
  "content": [
    {
      "id": 1,
      "employeeNumber": "EMP001",
      "name": "山田太郎",
      "email": "yamada@example.com",
      "department": {
        "id": 1,
        "name": "営業部",
        "code": "SALES"
      },
      "joinDate": "2025-12-01",
      "createdAt": "2025-12-01T00:00:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "currentPage": 0
}
```

**レスポンス項目**
| 項目名 | 型 | 説明 |
|--------|-----|------|
| content | Array<EmployeeDto> | 社員リスト |
| totalElements | Long | 総件数 |
| totalPages | Integer | 総ページ数 |
| currentPage | Integer | 現在のページ |

**エラー時**
| ステータスコード | エラーコード | 説明 |
|----------------|------------|------|
| 400 Bad Request | ERR003 | バリデーションエラー |

---

### 4.2 GET /api/v1/employees/{id}

#### 概要
指定したIDの社員情報を取得します。

#### リクエスト
**パスパラメータ**
| パラメータ名 | 型 | 必須 | 説明 |
|------------|-----|------|------|
| id | Long | 必須 | 社員ID |

**リクエスト例**
```
GET /api/v1/employees/1
```

#### レスポンス
**成功時（200 OK）**
```json
{
  "id": 1,
  "employeeNumber": "EMP001",
  "name": "山田太郎",
  "email": "yamada@example.com",
  "department": {
    "id": 1,
    "name": "営業部",
    "code": "SALES"
  },
  "joinDate": "2025-12-01",
  "createdAt": "2025-12-01T00:00:00"
}
```

**レスポンス項目**
| 項目名 | 型 | 説明 |
|--------|-----|------|
| id | Long | 社員ID |
| employeeNumber | String | 社員番号 |
| name | String | 社員名 |
| email | String | メールアドレス |
| department | DepartmentDto | 部署情報 |
| joinDate | LocalDate | 入社日 |
| createdAt | LocalDateTime | 作成日時 |

**エラー時**
| ステータスコード | エラーコード | 説明 |
|----------------|------------|------|
| 404 Not Found | ERR004 | 社員が見つかりません |

---

### 4.3 POST /api/v1/employees

#### 概要
新しい社員を作成します。

#### リクエスト
**パスパラメータ**
なし

**リクエストボディ**
```json
{
  "name": "山田太郎",
  "email": "yamada@example.com",
  "departmentId": 1,
  "joinDate": "2025-12-01"
}
```

**リクエスト項目**
| 項目名 | 型 | 必須 | 制約 | 説明 |
|--------|-----|------|------|------|
| name | String | 必須 | 1-100文字 | 社員名 |
| email | String | 必須 | メール形式 | メールアドレス |
| departmentId | Long | 必須 | 存在する部署ID | 部署ID |
| joinDate | LocalDate | 必須 | 有効な日付 | 入社日 |

**リクエスト例**
```
POST /api/v1/employees
Content-Type: application/json

{
  "name": "山田太郎",
  "email": "yamada@example.com",
  "departmentId": 1,
  "joinDate": "2025-12-01"
}
```

#### レスポンス
**成功時（201 Created）**
```json
{
  "id": 1,
  "employeeNumber": "EMP001",
  "name": "山田太郎",
  "email": "yamada@example.com",
  "department": {
    "id": 1,
    "name": "営業部",
    "code": "SALES"
  },
  "joinDate": "2025-12-01",
  "createdAt": "2025-12-01T00:00:00"
}
```

**レスポンス項目**
| 項目名 | 型 | 説明 |
|--------|-----|------|
| id | Long | 社員ID |
| employeeNumber | String | 社員番号（自動採番） |
| name | String | 社員名 |
| email | String | メールアドレス |
| department | DepartmentDto | 部署情報 |
| joinDate | LocalDate | 入社日 |
| createdAt | LocalDateTime | 作成日時 |

**エラー時**
| ステータスコード | エラーコード | 説明 |
|----------------|------------|------|
| 400 Bad Request | ERR003 | バリデーションエラー |
| 404 Not Found | ERR002 | 部署が存在しません |
| 409 Conflict | ERR001 | メールアドレスが重複しています |

---

### 4.4 PUT /api/v1/employees/{id}

#### 概要
既存の社員情報を更新します。

#### リクエスト
**パスパラメータ**
| パラメータ名 | 型 | 必須 | 説明 |
|------------|-----|------|------|
| id | Long | 必須 | 社員ID |

**リクエストボディ**
```json
{
  "name": "山田次郎",
  "email": "yamada2@example.com",
  "departmentId": 2
}
```

**リクエスト項目**
| 項目名 | 型 | 必須 | 制約 | 説明 |
|--------|-----|------|------|------|
| name | String | 任意 | 1-100文字 | 社員名 |
| email | String | 任意 | メール形式 | メールアドレス |
| departmentId | Long | 任意 | 存在する部署ID | 部署ID |

**リクエスト例**
```
PUT /api/v1/employees/1
Content-Type: application/json

{
  "name": "山田次郎",
  "email": "yamada2@example.com",
  "departmentId": 2
}
```

#### レスポンス
**成功時（200 OK）**
```json
{
  "id": 1,
  "employeeNumber": "EMP001",
  "name": "山田次郎",
  "email": "yamada2@example.com",
  "department": {
    "id": 2,
    "name": "開発部",
    "code": "DEV"
  },
  "joinDate": "2025-12-01",
  "createdAt": "2025-12-01T00:00:00"
}
```

**レスポンス項目**
GET /api/v1/employees/{id} と同じ

**エラー時**
| ステータスコード | エラーコード | 説明 |
|----------------|------------|------|
| 400 Bad Request | ERR003 | バリデーションエラー |
| 404 Not Found | ERR004 | 社員が見つかりません |
| 404 Not Found | ERR002 | 部署が存在しません |
| 409 Conflict | ERR001 | メールアドレスが重複しています |

---

### 4.5 DELETE /api/v1/employees/{id}

#### 概要
社員情報を論理削除します。

#### リクエスト
**パスパラメータ**
| パラメータ名 | 型 | 必須 | 説明 |
|------------|-----|------|------|
| id | Long | 必須 | 社員ID |

**リクエスト例**
```
DELETE /api/v1/employees/1
```

#### レスポンス
**成功時（204 No Content）**
レスポンスボディなし

**エラー時**
| ステータスコード | エラーコード | 説明 |
|----------------|------------|------|
| 404 Not Found | ERR004 | 社員が見つかりません |

---

### 4.6 GET /api/v1/departments

#### 概要
部署一覧を取得します。

#### リクエスト
**パスパラメータ**
なし

**クエリパラメータ**
なし

**リクエスト例**
```
GET /api/v1/departments
```

#### レスポンス
**成功時（200 OK）**
```json
[
  {
    "id": 1,
    "name": "営業部",
    "code": "SALES"
  },
  {
    "id": 2,
    "name": "開発部",
    "code": "DEV"
  }
]
```

**レスポンス項目**
| 項目名 | 型 | 説明 |
|--------|-----|------|
| - | Array<DepartmentDto> | 部署リスト |

**DepartmentDto**
| 項目名 | 型 | 説明 |
|--------|-----|------|
| id | Long | 部署ID |
| name | String | 部署名 |
| code | String | 部署コード |

**エラー時**
エラーなし（空配列を返却）

---

### 4.7 GET /api/v1/departments/{id}

#### 概要
指定したIDの部署情報を取得します。

#### リクエスト
**パスパラメータ**
| パラメータ名 | 型 | 必須 | 説明 |
|------------|-----|------|------|
| id | Long | 必須 | 部署ID |

**リクエスト例**
```
GET /api/v1/departments/1
```

#### レスポンス
**成功時（200 OK）**
```json
{
  "id": 1,
  "name": "営業部",
  "code": "SALES"
}
```

**レスポンス項目**
| 項目名 | 型 | 説明 |
|--------|-----|------|
| id | Long | 部署ID |
| name | String | 部署名 |
| code | String | 部署コード |

**エラー時**
| ステータスコード | エラーコード | 説明 |
|----------------|------------|------|
| 404 Not Found | ERR004 | 部署が見つかりません |

---

## 5. エラーコード一覧

| エラーコード | エラーメッセージ | HTTPステータス | 説明 |
|------------|----------------|---------------|------|
| ERR001 | メールアドレスが重複しています | 409 Conflict | 既に登録されているメールアドレス |
| ERR002 | 部署が存在しません | 404 Not Found | 存在しない部署ID |
| ERR003 | バリデーションエラー | 400 Bad Request | リクエストパラメータが不正 |
| ERR004 | リソースが見つかりません | 404 Not Found | 指定したIDのリソースが存在しない |
| ERR500 | 予期しないエラーが発生しました | 500 Internal Server Error | サーバー内部エラー |

---

## 6. データ型定義

### 6.1 EmployeeDto
```json
{
  "id": 1,
  "employeeNumber": "EMP001",
  "name": "山田太郎",
  "email": "yamada@example.com",
  "department": {
    "id": 1,
    "name": "営業部",
    "code": "SALES"
  },
  "joinDate": "2025-12-01",
  "createdAt": "2025-12-01T00:00:00"
}
```

### 6.2 DepartmentDto
```json
{
  "id": 1,
  "name": "営業部",
  "code": "SALES"
}
```

### 6.3 CreateEmployeeRequest
```json
{
  "name": "山田太郎",
  "email": "yamada@example.com",
  "departmentId": 1,
  "joinDate": "2025-12-01"
}
```

### 6.4 UpdateEmployeeRequest
```json
{
  "name": "山田次郎",
  "email": "yamada2@example.com",
  "departmentId": 2
}
```

### 6.5 ErrorResponse
```json
{
  "errorCode": "ERR001",
  "message": "メールアドレスが重複しています",
  "timestamp": "2025-12-01T00:00:00"
}
```

---

## 7. 変更履歴

| バージョン | 変更日 | 変更内容 | 変更者 |
|-----------|--------|---------|--------|
| 1.0 | 2025-12-01 | 初版作成 | [作成者名] |

