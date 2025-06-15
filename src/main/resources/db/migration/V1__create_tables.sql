-- テーブル: author（著者）
CREATE TABLE author (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    birth_date DATE NOT NULL,
    CONSTRAINT chk_birth_date CHECK (birth_date < CURRENT_DATE)
);

-- テーブル: book（書籍）
CREATE TABLE book (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL CHECK (price >= 0),
    publish_status VARCHAR(20) NOT NULL CHECK (publish_status IN ('UNPUBLISHED', 'PUBLISHED'))
);

-- テーブル: book_author（中間テーブル）
CREATE TABLE book_author (
    book_id INTEGER NOT NULL,
    author_id INTEGER NOT NULL,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE
);
