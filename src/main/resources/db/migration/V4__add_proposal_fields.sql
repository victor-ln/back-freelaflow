-- Adicionar campos faltantes na tabela proposal conforme o diagrama

ALTER TABLE proposal ADD COLUMN IF NOT EXISTS titulo VARCHAR(200);
ALTER TABLE proposal ADD COLUMN IF NOT EXISTS valor_total DECIMAL(10,2);
ALTER TABLE proposal ADD COLUMN IF NOT EXISTS contrato_status VARCHAR(50) DEFAULT 'Não Gerado';
ALTER TABLE proposal ADD COLUMN IF NOT EXISTS observacoes TEXT;
ALTER TABLE proposal ADD COLUMN IF NOT EXISTS evidencia_aceite VARCHAR(500);
ALTER TABLE proposal ADD COLUMN IF NOT EXISTS contrato_gerado VARCHAR(500);
ALTER TABLE proposal ADD COLUMN IF NOT EXISTS contrato_editado VARCHAR(500);
ALTER TABLE proposal ADD COLUMN IF NOT EXISTS data_edicao_contrato TIMESTAMP;
ALTER TABLE proposal ADD COLUMN IF NOT EXISTS email_enviado BOOLEAN DEFAULT FALSE;
ALTER TABLE proposal ADD COLUMN IF NOT EXISTS data_envio_email TIMESTAMP;
ALTER TABLE proposal ADD COLUMN IF NOT EXISTS template_id INT REFERENCES template(id);
ALTER TABLE proposal ADD COLUMN IF NOT EXISTS criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE proposal ADD COLUMN IF NOT EXISTS atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Renomear a coluna 'valor' para 'valor_total' se ainda existir como 'valor'
-- Essa linha pode falhar se já foi renomeada, por isso verificamos primeiro
DO $$
BEGIN
    IF EXISTS(
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='proposal'
        AND column_name='valor'
        AND table_schema='public'
    ) THEN
        ALTER TABLE proposal RENAME COLUMN valor TO valor_total;
    END IF;
END $$;
