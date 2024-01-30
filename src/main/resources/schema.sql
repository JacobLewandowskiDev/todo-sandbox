DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS todo;
DROP TYPE IF EXISTS priority_enum;

CREATE TYPE priority_enum AS ENUM ('LOW', 'MEDIUM', 'HIGH');

CREATE TABLE IF NOT EXISTS todo (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    priority priority_enum NOT NULL
);

CREATE INDEX idx_todo_name ON todo (name);

CREATE TABLE IF NOT EXISTS step (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    todo_id BIGINT NOT NULL REFERENCES todo (id) ON DELETE CASCADE
);

CREATE INDEX idx_step_todo_id ON step (todo_id);