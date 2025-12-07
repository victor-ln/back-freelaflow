
CREATE TABLE endereco (
    id BIGSERIAL PRIMARY KEY,
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
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    cpf_cnpj VARCHAR(20) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    endereco_id BIGINT REFERENCES endereco(id) ON DELETE CASCADE
);

CREATE TABLE client (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    cpf_cnpj VARCHAR(20) NOT NULL,
    telefone VARCHAR(30),
    endereco_id BIGINT REFERENCES endereco(id) ON DELETE CASCADE,
    freelancer_id BIGINT REFERENCES freelancer(id) ON DELETE CASCADE,
    CONSTRAINT unique_email_per_freelancer UNIQUE (email, freelancer_id),
    CONSTRAINT unique_cpf_per_freelancer UNIQUE (cpf_cnpj, freelancer_id)
);

CREATE TABLE role (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL,
    descricao TEXT,
    ativo BOOLEAN DEFAULT TRUE
);


CREATE TABLE freelancer_role (
    freelancer_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (freelancer_id, role_id),
    CONSTRAINT fk_freelancer FOREIGN KEY (freelancer_id) REFERENCES freelancer(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    freelancer_id BIGINT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE,
    CONSTRAINT unique_category_per_freelancer UNIQUE (tipo, freelancer_id)
);

CREATE TABLE service (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    valor DOUBLE PRECISION NOT NULL, -- Alterado de DECIMAL para DOUBLE PRECISION
    status VARCHAR(50) NOT NULL DEFAULT 'ATIVO',
    ativo BOOLEAN DEFAULT TRUE,
    categoria_id BIGINT NOT NULL REFERENCES category(id) ON DELETE CASCADE,
    freelancer_id BIGINT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_service_per_freelancer UNIQUE (nome, freelancer_id)
);

CREATE TABLE proposal (
    id BIGSERIAL PRIMARY KEY,
    descricao TEXT,
    valor DOUBLE PRECISION, -- Alterado de DECIMAL para DOUBLE PRECISION
    status VARCHAR(50) NOT NULL DEFAULT 'Pendente',
    cliente_id BIGINT REFERENCES client(id) ON DELETE CASCADE,
    freelancer_id BIGINT REFERENCES freelancer(id) ON DELETE CASCADE,
    categoria_id BIGINT REFERENCES category(id)
);


CREATE TABLE kanban (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    proposta_id BIGINT REFERENCES proposal(id) ON DELETE CASCADE,
    freelancer_id BIGINT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE
);

CREATE TABLE task (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    status VARCHAR(50) DEFAULT 'A Fazer',
    prioridade VARCHAR(50) DEFAULT 'Média',
    kanban_id BIGINT REFERENCES kanban(id) ON DELETE CASCADE,
    freelancer_id BIGINT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE
);

CREATE TABLE template  (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    filename VARCHAR(255) NOT NULL,
    filepath VARCHAR(500) NOT NULL,
    storage_type VARCHAR(50) DEFAULT 'LOCAL',
    freelancer_id BIGINT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contract (
    id BIGSERIAL PRIMARY KEY,
    nome_arquivo VARCHAR(255) NOT NULL,
    caminho_arquivo VARCHAR(500) NOT NULL,
    cliente_id BIGINT NOT NULL REFERENCES client(id) ON DELETE CASCADE,
    freelancer_id BIGINT NOT NULL REFERENCES freelancer(id) ON DELETE CASCADE,
    service_id BIGINT NOT NULL REFERENCES service(id) ON DELETE CASCADE,
    template_id BIGINT NOT NULL REFERENCES template(id),
    status VARCHAR(50) DEFAULT 'GERADO',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
