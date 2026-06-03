ALTER TABLE budget RENAME COLUMN year_month TO year_month_old;
ALTER TABLE budget ADD COLUMN year_month TIMESTAMP UNIQUE;
UPDATE budget SET year_month = year_month_old::timestamp;
ALTER TABLE budget DROP COLUMN year_month_old;

ALTER TABLE budget RENAME COLUMN created_at TO created_at_old;
ALTER TABLE budget ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE budget SET created_at = created_at_old;
ALTER TABLE budget DROP COLUMN created_at_old;

ALTER TABLE budget RENAME COLUMN updated_at TO updated_at_old;
ALTER TABLE budget ADD COLUMN updated_at TIMESTAMP;
UPDATE budget SET updated_at = updated_at_old;
ALTER TABLE budget DROP COLUMN updated_at_old;
