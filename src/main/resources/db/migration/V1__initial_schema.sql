-- =====================================================
-- FREELAFLOW - Schema Inicial Completo
-- Todas as tabelas conforme diagrama do banco de dados
-- =====================================================

-- Tabela: endereco
CREATE TABLE endereco (
    id SERIAL PRIMARY KEY,
    cep VARCHAR(20) NOT NULL,
    rua VARCHAR(150) NOT NULL,
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    pais VARCHAR(50) NOT NULL
);

-- Tabela: freelancer
CREATE TABLE freelancer (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    cpf_cnpj VARCHAR(20) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    endereco_id INT REFERENCES endereco(id) ON DELETE CASCADE
);

-- Tabela: client
CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    cpf_cnpj VARCHAR(20) NOT NULL,
    telefone VARCHAR(30),
    endereco_id INT REFERENCES endereco(id) ON DELETE CASCADE,
    freelancer_id INT REFERENCES freelancer(id) ON DELETE CASCADE,
    CONSTRAINT unique_email_per_freelancer UNIQUE (email, freelancer_id),
    CONSTRAINT unique_cpf_per_freelancer UNIQUE (cpf_cnpj, freelancer_id)
);

-- Tabela: role
CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL,
    descricao TEXT,
    ativo BOOLEAN DEFAULT TRUE
);

-- Tabela: freelancer_role (ManyToMany)
CREATE TABLE freelancer_role (
    freelancer_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (freelancer_id, role_id),
    CONSTRAINT fk_freelancer FOREIGN KEY (freelancer_id) REFERENCES freelancer(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

-- Tabela: category
CREATE TABLE category (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    freelancer_id INT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE,
    CONSTRAINT unique_category_per_freelancer UNIQUE (tipo, freelancer_id)
);

-- Tabela: service
CREATE TABLE service (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    valor DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ATIVO',
    ativo BOOLEAN DEFAULT TRUE,
    categoria_id INT NOT NULL REFERENCES category(id) ON DELETE CASCADE,
    freelancer_id INT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_service_per_freelancer UNIQUE (nome, freelancer_id)
);

-- Tabela: template
CREATE TABLE template (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    filename VARCHAR(255) NOT NULL,
    filepath VARCHAR(500) NOT NULL,
    storage_type VARCHAR(50) DEFAULT 'LOCAL',
    freelancer_id INT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela: proposal (com TODOS os campos do diagrama)
CREATE TABLE proposal (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(200),
    descricao TEXT,
    valor_total DECIMAL(10,2),
    status VARCHAR(50) NOT NULL DEFAULT 'Pendente',
    contrato_status VARCHAR(50) DEFAULT 'Não Gerado',
    observacoes TEXT,
    evidencia_aceite VARCHAR(500),
    contrato_gerado VARCHAR(500),
    contrato_editado VARCHAR(500),
    data_edicao_contrato TIMESTAMP,
    email_enviado BOOLEAN DEFAULT FALSE,
    data_envio_email TIMESTAMP,
    cliente_id INT REFERENCES client(id) ON DELETE CASCADE,
    freelancer_id INT REFERENCES freelancer(id) ON DELETE CASCADE,
    categoria_id INT REFERENCES category(id),
    template_id INT REFERENCES template(id),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela: proposal_service (ManyToMany entre Proposal e Service)
CREATE TABLE proposal_service (
    proposal_id INT NOT NULL,
    service_id INT NOT NULL,
    PRIMARY KEY (proposal_id, service_id),
    CONSTRAINT fk_proposal FOREIGN KEY (proposal_id) REFERENCES proposal(id) ON DELETE CASCADE,
    CONSTRAINT fk_service FOREIGN KEY (service_id) REFERENCES service(id) ON DELETE CASCADE
);

-- Tabela: kanban
CREATE TABLE kanban (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    proposta_id INT REFERENCES proposal(id) ON DELETE CASCADE,
    freelancer_id INT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE
);

-- Tabela: task
CREATE TABLE task (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    status VARCHAR(50) DEFAULT 'A Fazer',
    prioridade VARCHAR(50) DEFAULT 'Média',
    kanban_id INT REFERENCES kanban(id) ON DELETE CASCADE,
    freelancer_id INT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE
);

-- Tabela: contract
CREATE TABLE contract (
    id SERIAL PRIMARY KEY,
    nome_arquivo VARCHAR(255) NOT NULL,
    caminho_arquivo VARCHAR(500) NOT NULL,
    cliente_id INT NOT NULL REFERENCES client(id) ON DELETE CASCADE,
    freelancer_id INT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE,
    service_id INT NOT NULL REFERENCES service(id) ON DELETE CASCADE,
    template_id INT NOT NULL REFERENCES template(id),
    status VARCHAR(50) DEFAULT 'GERADO',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para otimização de consultas
CREATE INDEX idx_client_freelancer ON client(freelancer_id);
CREATE INDEX idx_service_freelancer ON service(freelancer_id);
CREATE INDEX idx_service_categoria ON service(categoria_id);
CREATE INDEX idx_proposal_freelancer ON proposal(freelancer_id);
CREATE INDEX idx_proposal_cliente ON proposal(cliente_id);
CREATE INDEX idx_proposal_status ON proposal(status);
CREATE INDEX idx_contract_freelancer ON contract(freelancer_id);
CREATE INDEX idx_contract_status ON contract(status);
CREATE INDEX idx_kanban_freelancer ON kanban(freelancer_id);
CREATE INDEX idx_task_kanban ON task(kanban_id);
