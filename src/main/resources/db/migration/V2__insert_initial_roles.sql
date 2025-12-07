-- Inserir roles iniciais do sistema
INSERT INTO role (nome, descricao, ativo) VALUES
('ADMIN', 'Administrador do sistema com acesso total', TRUE),
('USER', 'Usuário padrão do sistema', TRUE),
('FREELANCER', 'Freelancer com acesso às funcionalidades principais', TRUE);
