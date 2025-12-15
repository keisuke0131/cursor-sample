# DTO作成

リクエスト/レスポンス用のDTOクラスを作成してください。

## 実装方針
- @.cursor/rules/coding-standards.md のコーディング規約に従う
- パッケージ: `com.company.system.dto`
- 命名規則:
  - リクエスト: `[操作名][エンティティ名]Request`（例: `CreateEmployeeRequest`）
  - レスポンス: `[エンティティ名]Dto`（例: `EmployeeDto`）
- Lombokの`@Data`または`@Getter`/`@Setter`を使用
- バリデーションアノテーションを適切に追加
- `@JsonInclude(JsonInclude.Include.NON_NULL)`を検討

## 実装内容
1. DTOクラスの作成
2. フィールドの定義
3. バリデーションアノテーションの追加
4. 必要に応じて`@Builder`パターンの実装

## 設計書参照
@docs/05_サンプルシステム設計書.md のAPI設計を参照してください。

