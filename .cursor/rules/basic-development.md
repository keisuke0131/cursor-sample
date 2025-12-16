---
description: "社員管理システムの基本開発ルール: プロジェクト概要、アーキテクチャ原則、基本方針"
alwaysApply: true
---

# 基本開発ルール

## プロジェクト概要

このプロジェクトは、社員情報を管理する簡易な社内システムです。
- 言語: Java 17
- フレームワーク: Spring Boot 3.2.0
- アーキテクチャ: 3層アーキテクチャ（Controller-Service-Repository）
- データベース: H2 Database
- テストフレームワーク: JUnit 5, Mockito

## アーキテクチャ原則

### レイヤー構造
```
Controller層 (REST API)
  ↓
Service層 (ビジネスロジック)
  ↓
Repository層 (データアクセス)
  ↓
Database (H2)
```

### 責務の分離
- **Controller層**: HTTPリクエストの受け取り、バリデーション、レスポンス返却のみ。ビジネスロジックは含めない。
- **Service層**: ビジネスロジックの実装、トランザクション管理。データベースアクセスはRepositoryに委譲。
- **Repository層**: データベースアクセスのみ。ビジネスロジックは含めない。
- **Model層**: エンティティとDTOを分離。エンティティを直接返却しない。

## パッケージ構造

```
com.company.system
├── controller/    # REST APIコントローラー
├── service/       # ビジネスロジック
├── repository/    # データアクセス
├── model/         # エンティティ
├── dto/           # データ転送オブジェクト
└── exception/     # 例外クラス
```

## ビジネスルール

### 社員管理
- 社員番号は自動採番（形式: EMP001, EMP002, ...）
- メールアドレスは重複不可
- 削除は論理削除のみ（`deleted_at`を設定）
- 削除された社員は取得・一覧表示の対象外

### 部署管理
- 部署名・部署コードはユニーク
- 部署に所属する社員が存在する場合、部署は削除不可

## データベース設計ルール

### テーブル設計
- 主キーは`id`（BIGINT、自動採番）
- 作成日時・更新日時を必須で持つ（`created_at`, `updated_at`）
- 論理削除用の`deleted_at`フィールドを用意
- 外部キー制約を適切に設定

### インデックス
- 検索で使用されるカラムにインデックスを設定
- ユニーク制約のカラムにインデックスを設定

### クエリ
- 論理削除のチェックをクエリに含める
- N+1問題を回避（`JOIN FETCH`を使用）
- ページネーションを使用

## API設計ルール

### RESTful API
- ベースパス: `/api/v1`
- HTTPメソッドを適切に使用（GET, POST, PUT, DELETE）
- リソース名は複数形（例: `/employees`, `/departments`）
- 適切なHTTPステータスコードを返却

### エラーレスポンス形式
```json
{
  "errorCode": "ERR001",
  "message": "エラーメッセージ",
  "timestamp": "2025-12-01T00:00:00"
}
```

## 例外処理

### カスタム例外クラス
- `ResourceNotFoundException`: リソースが見つからない場合（404 Not Found）
- `DuplicateResourceException`: 重複エラー（409 Conflict）
- `ValidationException`: バリデーションエラー（400 Bad Request）
- `InternalServerException`: サーバーエラー（500 Internal Server Error）

### グローバル例外ハンドラー
- `@RestControllerAdvice`を使用
- 適切なHTTPステータスコードとエラーレスポンスを返却
- ログ出力を実装

@src/main/java/com/company/system/exception/GlobalExceptionHandler.java

## ログ出力

### ログレベル
- ERROR: エラー発生時
- WARN: 警告発生時
- INFO: 重要な処理の開始・終了
- DEBUG: デバッグ情報

### ログ出力箇所
- リクエスト受信時（INFO）
- エラー発生時（ERROR）
- ビジネスロジックの重要ポイント（INFO）

## 依存関係

### 必須ライブラリ
- Spring Boot Web
- Spring Boot Data JPA
- Spring Boot Validation
- H2 Database
- Lombok
- JUnit 5
- Mockito

### Lombokの使用
- `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`を使用
- `@RequiredArgsConstructor`でコンストラクタインジェクション
- `@Slf4j`でログ出力

## コード生成時の注意事項

1. **仕様書を参照**: 実装前に`specification/`配下の仕様書を確認
2. **設計書を参照**: 実装前に`docs/`配下の設計書を確認
3. **既存コードに合わせる**: 既存のコードスタイルに合わせる
4. **テストを書く**: 新しい機能には必ず単体テストを実装
5. **JavaDocを記述**: すべてのpublicクラス・メソッドにJavaDocを記述
6. **例外処理を実装**: 適切な例外処理とエラーハンドリングを実装
7. **ログを出力**: 重要な処理にはログ出力を実装

## ファイル作成時のルール

### 新しい機能を追加する場合
1. 仕様書を確認
2. エンティティを実装（必要に応じて）
3. Repositoryを実装
4. Serviceを実装
5. Controllerを実装
6. DTOを実装
7. 単体テストを実装

### ファイル命名規則
- Javaクラス: PascalCase（例: `EmployeeService.java`）
- テストクラス: `[クラス名]Test.java`（例: `EmployeeServiceTest.java`）
- マークダウンファイル: `[番号]_[名前].md`

## 参考ドキュメント

- 仕様書: `specification/`配下
- 設計書: `docs/`配下
- テスト設計書: `test-docs/`配下


