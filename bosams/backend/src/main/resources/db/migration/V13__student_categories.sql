CREATE TABLE IF NOT EXISTS student_category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

INSERT INTO student_category (name, status)
VALUES
    ('General', 'ACTIVE'),
    ('OBC', 'ACTIVE'),
    ('ST', 'ACTIVE'),
    ('SC', 'ACTIVE')
ON CONFLICT (name) DO UPDATE
SET status = 'ACTIVE';
