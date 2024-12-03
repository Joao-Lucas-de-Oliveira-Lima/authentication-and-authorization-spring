CREATE TABLE users
(
    user_id                    SERIAL PRIMARY KEY,
    username                   VARCHAR(255) UNIQUE NOT NULL,
    password                   VARCHAR(255)        NOT NULL,
    is_account_non_expired     BOOLEAN             NOT NULL DEFAULT TRUE,
    is_account_non_locked      BOOLEAN             NOT NULL DEFAULT TRUE,
    is_credentials_non_expired BOOLEAN             NOT NULL DEFAULT TRUE,
    is_enabled                 BOOLEAN             NOT NULL DEFAULT TRUE
);
