CREATE DATABASE IF NOT EXISTS demo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE demo_db;

CREATE TABLE IF NOT EXISTS user_info (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    user_name VARCHAR(100) NOT NULL COMMENT 'User name',
    email VARCHAR(255) NOT NULL COMMENT 'Email address',
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_info_email (email)
) DEFAULT CHARSET=utf8mb4 COMMENT='Demo users';

INSERT INTO user_info (user_name, email)
SELECT 'Alice', 'alice@example.com'
WHERE NOT EXISTS (SELECT 1 FROM user_info WHERE email = 'alice@example.com');
