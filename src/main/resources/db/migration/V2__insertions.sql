-- File: src/main/resources/db/migration/V2__insert_sample_data.sql
INSERT INTO usuario(nome, email, senha, tipo_usuario) VALUES ('Admin', 'admin@gmail.com', '@123', 'ADMIN');
INSERT INTO filme(titulo) VALUES ('Filme Teste');
INSERT INTO usuario(nome, email, senha, tipo_usuario) VALUES ('Admin', 'usuario@gmail.com', '@124', 'COMUM');
