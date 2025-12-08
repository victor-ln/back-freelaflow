-- Add template reference to proposal table
ALTER TABLE proposal ADD COLUMN template_id BIGINT REFERENCES template(id);

-- Add title field to proposal (titulo)
ALTER TABLE proposal ADD COLUMN titulo VARCHAR(200);

-- Add timestamp columns
ALTER TABLE proposal ADD COLUMN criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE proposal ADD COLUMN atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
