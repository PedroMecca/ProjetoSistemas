-- ===================================================
-- SCRIPT SQL - Sistema de Recomendação de Filmes
-- Autor: Pedro Bonelli
-- Data: 2025-10-13
-- Banco: MySQL
-- ===================================================

-- Criação do Banco de Dados
CREATE DATABASE IF NOT EXISTS recomendacao_filmes;
USE recomendacao_filmes;

-- ===================================================
-- TABELA ESTADO
-- ===================================================
CREATE TABLE estado (
    id_estado INT AUTO_INCREMENT PRIMARY KEY,
    nome_estado VARCHAR(50) NOT NULL,
    uf CHAR(2) NOT NULL
);

-- ===================================================
-- TABELA USUARIO
-- ===================================================
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    tipo_usuario ENUM('ADMIN', 'COMUM') NOT NULL,
    id_estado INT,
    FOREIGN KEY (id_estado) REFERENCES estado(id_estado)
);

-- ===================================================
-- TABELA FILME
-- ===================================================
CREATE TABLE filme (
    id_filme INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    genero VARCHAR(50),
    ano_lancamento INT,
    id_usuario_admin INT NOT NULL,
    FOREIGN KEY (id_usuario_admin) REFERENCES usuario(id_usuario)
);

-- ===================================================
-- TABELA AVALIACAO
-- ===================================================
CREATE TABLE avaliacao (
    id_avaliacao INT AUTO_INCREMENT PRIMARY KEY,
    nota DECIMAL(2,1) CHECK (nota BETWEEN 0 AND 5),
    comentario TEXT,
    data_avaliacao DATE NOT NULL,
    id_usuario_comum INT NOT NULL,
    id_filme INT NOT NULL,
    FOREIGN KEY (id_usuario_comum) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_filme) REFERENCES filme(id_filme)
);

-- ===================================================
-- TABELA FAVORITO
-- ===================================================
CREATE TABLE favorito (
    id_favorito INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario_comum INT NOT NULL,
    id_filme INT NOT NULL,
    id_estado INT NOT NULL,
    data_favorito DATE,
    FOREIGN KEY (id_usuario_comum) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_filme) REFERENCES filme(id_filme),
    FOREIGN KEY (id_estado) REFERENCES estado(id_estado)
);
