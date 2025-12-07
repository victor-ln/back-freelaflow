-- Adiciona coluna de status ao template para workflow de aprovação
ALTER TABLE template
ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'EM_REVISAO';

-- Comentário para documentar os possíveis valores
COMMENT ON COLUMN template.status IS 'Status do template: EM_REVISAO, APROVADO, REJEITADO';
