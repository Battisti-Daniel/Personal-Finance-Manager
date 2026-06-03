-- EXPENSE categories: Mercado, Cinema, Parque, Circo
-- INCOME categories: Obra, Padaria

-- Obra - INCOME (77345f65-0483-443e-8898-e78e67c6bed8)
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '77345f65-0483-443e-8898-e78e67c6bed8', 'Pagamento obra cliente', 3500.00, 'INCOME', '2026-06-01', 'Cliente João', NOW());
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '77345f65-0483-443e-8898-e78e67c6bed8', 'Adiantamento reforma', 1500.00, 'INCOME', '2026-06-10', null, NOW());
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '77345f65-0483-443e-8898-e78e67c6bed8', 'Pagamento final obra', 2000.00, 'INCOME', '2026-06-20', 'Quitação total', NOW());

-- Padaria - INCOME (ec530d29-5a99-4331-9333-dea8be7bfca8)
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', 'ec530d29-5a99-4331-9333-dea8be7bfca8', 'Venda pães do dia', 320.00, 'INCOME', '2026-06-02', null, NOW());
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', 'ec530d29-5a99-4331-9333-dea8be7bfca8', 'Encomenda bolo festa', 450.00, 'INCOME', '2026-06-08', null, NOW());
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', 'ec530d29-5a99-4331-9333-dea8be7bfca8', 'Venda fim de semana', 680.00, 'INCOME', '2026-06-15', null, NOW());

-- Mercado - EXPENSE (5ebcfebe-bfdb-4419-8696-60f3569ccdbb)
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '5ebcfebe-bfdb-4419-8696-60f3569ccdbb', 'Compras da semana', 245.80, 'EXPENSE', '2026-06-02', 'Pão de Açúcar', NOW());
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '5ebcfebe-bfdb-4419-8696-60f3569ccdbb', 'Feira livre', 98.30, 'EXPENSE', '2026-06-07', null, NOW());
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '5ebcfebe-bfdb-4419-8696-60f3569ccdbb', 'Mercado mensal', 420.00, 'EXPENSE', '2026-06-15', 'Estoque do mês', NOW());

-- Cinema - EXPENSE (7222f428-085a-476d-923d-8b083bf47977)
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '7222f428-085a-476d-923d-8b083bf47977', 'Ingresso cinema', 35.00, 'EXPENSE', '2026-06-03', null, NOW());
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '7222f428-085a-476d-923d-8b083bf47977', 'Cinema + pipoca', 58.50, 'EXPENSE', '2026-06-12', 'Filme novo', NOW());
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '7222f428-085a-476d-923d-8b083bf47977', 'Sessão especial', 45.00, 'EXPENSE', '2026-06-20', null, NOW());

-- Parque - EXPENSE (89882792-5f3b-4f1d-ad68-949c5cdadcc0)
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '89882792-5f3b-4f1d-ad68-949c5cdadcc0', 'Passeio no parque', 20.00, 'EXPENSE', '2026-06-04', null, NOW());
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '89882792-5f3b-4f1d-ad68-949c5cdadcc0', 'Aluguel bicicleta', 40.00, 'EXPENSE', '2026-06-11', null, NOW());
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '89882792-5f3b-4f1d-ad68-949c5cdadcc0', 'Lanche no parque', 28.50, 'EXPENSE', '2026-06-18', null, NOW());

-- Circo - EXPENSE (6650a66e-25f3-4d33-9c8d-a84c78c16306)
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '6650a66e-25f3-4d33-9c8d-a84c78c16306', 'Ingresso circo', 60.00, 'EXPENSE', '2026-06-06', null, NOW());
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '6650a66e-25f3-4d33-9c8d-a84c78c16306', 'Pipoca e algodão doce', 25.00, 'EXPENSE', '2026-06-06', null, NOW());
INSERT INTO transactions (id, user_id, category_id, description, amount, type, date, notes, created_at) VALUES (gen_random_uuid(), '219ce5b8-d134-4582-ab1d-81f9c24a6e4c', '6650a66e-25f3-4d33-9c8d-a84c78c16306', 'Segundo ingresso', 60.00, 'EXPENSE', '2026-06-22', null, NOW());
