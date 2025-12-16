-- 部署の初期データ
INSERT INTO departments (id, name, code, created_at, updated_at) VALUES
(1, '営業部', 'SALES', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '開発部', 'DEV', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '人事部', 'HR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- シーケンスのリセット（H2 Database用）
ALTER TABLE departments ALTER COLUMN id RESTART WITH 4;
ALTER TABLE employees ALTER COLUMN id RESTART WITH 1;


