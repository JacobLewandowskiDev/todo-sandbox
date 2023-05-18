DROP TABLE IF EXISTS tododb.step;
DROP TABLE IF EXISTS tododb.todo;
DROP TYPE IF EXISTS tododb.priority_enum;

CREATE TYPE tododb.priority_enum AS ENUM ('LOW', 'MEDIUM', 'HIGH');

CREATE TABLE IF NOT EXISTS tododb.todo (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    priority tododb.priority_enum
);

CREATE TABLE IF NOT EXISTS tododb.step (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    done BOOLEAN DEFAULT FALSE,
    todo_id BIGINT NOT NULL,
    FOREIGN KEY (todo_id) REFERENCES tododb.todo (id)
);