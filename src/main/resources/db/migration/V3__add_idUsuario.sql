-- File: src/main/resources/db/migration/V3__add_id_usuario_admin.sql
ALTER TABLE filme ADD COLUMN id_usuario_admin BIGINT; -- nullable

-- se houver um usuário admin já inserido nas migrations anteriores, atribui aos filmes esse admin
UPDATE filme SET id_usuario_admin = (
    SELECT id FROM usuario WHERE tipo_usuario = 'ADMIN' LIMIT 1
) WHERE id_usuario_admin IS NULL;

ALTER TABLE filme ALTER COLUMN id_usuario_admin SET NOT NULL;

ALTER TABLE filme
    ADD CONSTRAINT fk_filme_usuario_admin
    FOREIGN KEY (id_usuario_admin) REFERENCES usuario(id);
