CREATE TABLE IF NOT EXISTS gl4dius_metadata
(
    key   TEXT PRIMARY KEY,
    value TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS sessions
(
    id             VARCHAR(36)  NOT NULL PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    description    TEXT,
    mode           VARCHAR(255) NOT NULL,
    config         TEXT,
    created_at     TEXT         NOT NULL,
    last_update_at TEXT
);
