CREATE TABLE budget (
    id UUID PRIMARY KEY,
    amount NUMERIC(15, 2) NOT NULL,
    year_month TIMESTAMP UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
