SET search_path TO tododb;

DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS todo;
DROP TYPE IF EXISTS priority_enum;

CREATE TYPE priority_enum AS ENUM ('LOW', 'MEDIUM', 'HIGH');

CREATE TABLE IF NOT EXISTS todo (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    priority priority_enum
);

CREATE TABLE IF NOT EXISTS step (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    done BOOLEAN DEFAULT FALSE,
    todo_id BIGINT NOT NULL,
    FOREIGN KEY (todo_id) REFERENCES todo (id)
);