# リポジトリ作成

データアクセス層のリポジトリインターフェースを作成してください。

## 実装方針
- @.cursor/rules/layer-implementation.md のリポジトリ層ガイドラインに従う
- @.cursor/rules/coding-standards.md のコーディング規約に従う
- パッケージ: `com.company.system.repository`
- Spring Data JPAの`JpaRepository`を継承
- 必要に応じてカスタムクエリメソッドを定義
- 命名規則: `[エンティティ名]Repository`（例: `EmployeeRepository`）

## 実装内容
1. リポジトリインターフェースの作成
2. `JpaRepository<Entity, ID>`の継承
3. 必要に応じてカスタムクエリメソッドの定義
4. `@Query`アノテーションを使用したJPQL/Nativeクエリ（必要に応じて）

## 設計書参照
@docs/05_サンプルシステム設計書.md のデータベース設計を参照してください。


