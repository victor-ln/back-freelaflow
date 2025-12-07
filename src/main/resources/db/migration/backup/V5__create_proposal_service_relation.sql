-- Criar tabela de relacionamento ManyToMany entre Proposal e Service
-- conforme indicado no diagrama (servicos_ids)

CREATE TABLE IF NOT EXISTS proposal_service (
    proposal_id INT NOT NULL,
    service_id INT NOT NULL,
    PRIMARY KEY (proposal_id, service_id),
    CONSTRAINT fk_proposal FOREIGN KEY (proposal_id) REFERENCES proposal(id) ON DELETE CASCADE,
    CONSTRAINT fk_service FOREIGN KEY (service_id) REFERENCES service(id) ON DELETE CASCADE
);
