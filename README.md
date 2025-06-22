# 書籍・著者管理API（Book & Author Management API）

このアプリケーションは、Kotlin + Spring Boot + jOOQ を使用して実装した書籍・著者管理のRESTful APIです。著者情報・書籍情報の登録・更新・取得を行う機能を備えています。

---

## 技術スタック

- Kotlin 1.9.25
- Spring Boot 3.5.0
- Spring MVC / Validation
- jOOQ + PostgreSQL
- Flyway（マイグレーション）
- Gradle (Kotlin DSL)
- JUnit5 + MockK + Spring MockMvc（テスト）

---

## 機能一覧

### 著者（Author）

| メソッド | エンドポイント           | 内容                        |
|----------|--------------------------|-----------------------------|
| POST     | `/api/authors`           | 著者の新規登録              |
| PUT      | `/api/authors/{id}`      | 著者情報の更新              |
| GET      | `/api/authors/{id}/books`| 指定著者に紐づく書籍の取得  |

### 書籍（Book）

| メソッド | エンドポイント           | 内容                        |
|----------|--------------------------|-----------------------------|
| POST     | `/api/books`             | 書籍の新規登録              |
| PUT      | `/api/books/{id}`        | 書籍情報の更新              |

---

## バリデーションルール

### BookRequest

- タイトル: 空文字不可
- 価格: 0以上の整数
- 出版ステータス: `PUBLISHED` または `UNPUBLISHED`
- 著者IDリスト: 1人以上指定必須

### AuthorRequest

- 名前: 空文字不可
- 生年月日: 過去日付である必要あり

---

## テスト

- Service層とController層のユニットテストを実装済み
- ドメインロジック（Book）のバリデーション・振る舞いの単体テストを実装
- テスト結果はbuild/reports/tests/test/index.htmlで出力
- テスト実行：

```bash
./gradlew test
