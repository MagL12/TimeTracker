CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS tasks (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) UNIQUE NOT NULL,
    start_time TIMESTAMP NOT NULL,
    stop_time TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'Активна'
    );

