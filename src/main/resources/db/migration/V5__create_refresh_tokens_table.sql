CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    device_id VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL,
    expires_at TIMESTAMP,
    created_at TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);
