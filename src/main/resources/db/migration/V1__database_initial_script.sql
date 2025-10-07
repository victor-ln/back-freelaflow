
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

CREATE TABLE freelancer (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    cpf_cnpj VARCHAR(20) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    endereco_id INT REFERENCES endereco(id) ON DELETE CASCADE
);

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

CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL,
    descricao TEXT,
    ativo BOOLEAN DEFAULT TRUE
);


CREATE TABLE freelancer_role (
    freelancer_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (freelancer_id, role_id),
    CONSTRAINT fk_freelancer FOREIGN KEY (freelancer_id) REFERENCES freelancer(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

CREATE TABLE category (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    freelancer_id INT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE,
    CONSTRAINT unique_category_per_freelancer UNIQUE (tipo, freelancer_id)
);

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

CREATE TABLE proposal (
    id SERIAL PRIMARY KEY,
    descricao TEXT,
    valor DECIMAL(10,2),
    status VARCHAR(50) NOT NULL DEFAULT 'Pendente',
    cliente_id INT REFERENCES client(id) ON DELETE CASCADE,
    freelancer_id INT REFERENCES freelancer(id) ON DELETE CASCADE,
    categoria_id INT REFERENCES category(id)
);


CREATE TABLE kanban (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    proposta_id INT REFERENCES proposal(id) ON DELETE CASCADE,
    freelancer_id INT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE
);

CREATE TABLE task (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    status VARCHAR(50) DEFAULT 'A Fazer',
    prioridade VARCHAR(50) DEFAULT 'Média',
    kanban_id INT REFERENCES kanban(id) ON DELETE CASCADE,
    freelancer_id INT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE
);

CREATE TABLE template  (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    filename VARCHAR(255) NOT NULL,
    filepath VARCHAR(500) NOT NULL,
    storage_type VARCHAR(50) DEFAULT 'LOCAL',
    freelancer_id INT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

---- Função genérica para atualizar o campo atualizado_em
--CREATE OR REPLACE FUNCTION update_timestamp()
--RETURNS TRIGGER AS $$
--BEGIN
--    NEW.atualizado_em = NOW();
--    RETURN NEW;
--END;
--$$ LANGUAGE plpgsql;
--
---- Trigger para service
--CREATE TRIGGER trg_service_update
--BEFORE UPDATE ON service
--FOR EACH ROW
--EXECUTE FUNCTION update_timestamp();
--
---- Trigger para contract
--CREATE TRIGGER trg_contract_update
--BEFORE UPDATE ON contract
--FOR EACH ROW
--EXECUTE FUNCTION update_timestamp();
--
---- Trigger para template
--CREATE TRIGGER trg_template_update
--BEFORE UPDATE ON template
--FOR EACH ROW
--EXECUTE FUNCTION update_timestamp();
--
---- Trigger para kanban
--CREATE TRIGGER trg_kanban_update
--BEFORE UPDATE ON kanban
--FOR EACH ROW
--EXECUTE FUNCTION update_timestamp();
--
---- Trigger para task
--CREATE TRIGGER trg_task_update
--BEFORE UPDATE ON task
--FOR EACH ROW
--EXECUTE FUNCTION update_timestamp();
--
---- Trigger para proposal
--CREATE TRIGGER trg_proposal_update
--BEFORE UPDATE ON proposal
--FOR EACH ROW
--EXECUTE FUNCTION update_timestamp();