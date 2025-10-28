-- File: src/main/resources/db/migration/V2__insert_sample_data.sql
INSERT INTO usuario(nome, email, senha, tipo_usuario) VALUES ('Admin', 'admin@gmail.com', '$2a$10$HiUIoSbMlYheywZA5ZYKaOqRFCvqOv.iYY6..hfj1.5C5jqaN5ype', 'ADMIN');
INSERT INTO filme(titulo) VALUES ('Filme Teste');
INSERT INTO usuario(nome, email, senha, tipo_usuario) VALUES ('Admin', 'usuario@gmail.com', '$2a$10$odD2BhYrQLE.PasTydvilOMS0twuwmY7IfH6A479qlmJqSLFVB2l6', 'COMUM');

--'$2a$10$HiUIoSbMlYheywZA5ZYKaOqRFCvqOv.iYY6..hfj1.5C5jqaN5ype'
-- '$2a$10$odD2BhYrQLE.PasTydvilOMS0twuwmY7IfH6A479qlmJqSLFVB2l6'
-- http://localhost:8080/h2-console