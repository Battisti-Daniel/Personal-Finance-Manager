CREATE TABLE categories (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    budget_id UUID,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,
    color VARCHAR(7) NOT NULL,
    icon VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_categories_budget FOREIGN KEY (budget_id) REFERENCES budget(id)
);
