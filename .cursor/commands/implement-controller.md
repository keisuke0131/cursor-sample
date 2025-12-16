# コントローラー層実装

REST APIコントローラーを実装してください。

## 実装方針
- @.cursor/rules/layer-implementation.md のコントローラー層ガイドラインに従う
- @.cursor/rules/coding-standards.md のコーディング規約に従う
- パッケージ: `com.company.system.controller`
- Spring Bootの`@RestController`を使用
- RESTful APIの設計原則に従う
- 適切なHTTPステータスコードを返す
- バリデーションを実装
- 例外処理は`@ExceptionHandler`または`GlobalExceptionHandler`を使用

## 実装内容
1. コントローラークラスの作成
2. エンドポイントの定義（GET, POST, PUT, DELETE）
3. リクエスト/レスポンスDTOの使用
4. バリデーションアノテーションの追加
5. サービス層の呼び出し
6. 適切なHTTPステータスコードの設定

## 設計書参照
@docs/05_サンプルシステム設計書.md を参照して実装してください。


