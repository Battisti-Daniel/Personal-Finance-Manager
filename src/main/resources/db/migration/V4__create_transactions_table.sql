CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    category_id UUID NOT NULL,
    description VARCHAR(200) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    date DATE,
    notes VARCHAR(255),
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories(id)
);
