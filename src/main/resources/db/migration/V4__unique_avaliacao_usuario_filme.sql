ALTER TABLE avaliacao
ADD CONSTRAINT uk_avaliacao_usuario_filme
UNIQUE (usuario_id, filme_id);
