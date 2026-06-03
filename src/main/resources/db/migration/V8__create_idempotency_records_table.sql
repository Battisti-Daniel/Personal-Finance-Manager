CREATE TABLE idempotency_records (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idem_key     VARCHAR(255) NOT NULL,
    user_id      UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    response_body TEXT        NOT NULL,
    status_code  INT          NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT uq_idem_key_user UNIQUE (idem_key, user_id)
);
