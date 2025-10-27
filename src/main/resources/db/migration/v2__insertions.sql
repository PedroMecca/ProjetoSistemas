-- ===================================================
-- INSERÇÕES EXEMPLO (OPCIONAIS)
-- ===================================================
INSERT INTO estado (nome_estado, uf) VALUES
('São Paulo', 'SP'),
('Rio de Janeiro', 'RJ'),
('Minas Gerais', 'MG');

INSERT INTO usuario (nome, email, senha, tipo_usuario, id_estado) VALUES
('Admin Pedro', 'admin@filmes.com', '123456', 'ADMIN', 1),
('João Silva', 'joao@gmail.com', 'abc123', 'COMUM', 2),
('Maria Souza', 'maria@gmail.com', 'xyz789', 'COMUM', 3);

INSERT INTO filme (titulo, descricao, genero, ano_lancamento, id_usuario_admin) VALUES
('Inception', 'Um ladrão invade sonhos.', 'Ficção', 2010, 1),
('Interestelar', 'Exploração espacial e tempo.', 'Ficção', 2014, 1);

INSERT INTO avaliacao (nota, comentario, data_avaliacao, id_usuario_comum, id_filme) VALUES
(4.5, 'Excelente!', '2025-10-10', 2, 1),
(5.0, 'Obra-prima!', '2025-10-11', 3, 2);

INSERT INTO favorito (id_usuario_comum, id_filme, id_estado, data_favorito) VALUES
(2, 1, 2, '2025-10-10'),
(3, 2, 3, '2025-10-11');