-- Remove budget_id FK de categories (relação antiga)
ALTER TABLE categories DROP COLUMN IF EXISTS budget_id;

-- Remove budget_id FK de users (relação antiga)
ALTER TABLE users DROP COLUMN IF EXISTS budget_id;

-- Recria a tabela budget com o novo modelo (budget por categoria/mês)
DROP TABLE IF EXISTS budget;

CREATE TABLE budget (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id UUID        NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    year_month  VARCHAR(7)  NOT NULL,
    amount      NUMERIC(15, 2) NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP,
    CONSTRAINT uq_budget_category_month UNIQUE (category_id, year_month)
);
