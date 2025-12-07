-- Corrigir o tipo de dados do campo descricao na tabela proposal
-- O campo estava sendo interpretado como BYTEA, mas deve ser TEXT

ALTER TABLE proposal ALTER COLUMN descricao TYPE TEXT;
