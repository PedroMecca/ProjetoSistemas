-- Admin principal (id = 1)
INSERT INTO usuario(nome, email, senha, tipo_usuario)
VALUES (
    'Admin',
    'admin@gmail.com',
    '$2a$10$HiUIoSbMlYheywZA5ZYKaOqRFCvqOv.iYY6..hfj1.5C5jqaN5ype',
    'ADMIN'
);

-- Usuário comum (id = 2)
INSERT INTO usuario(nome, email, senha, tipo_usuario)
VALUES (
    'Usuario Comum',
    'usuario@gmail.com',
    '$2a$10$odD2BhYrQLE.PasTydvilOMS0twuwmY7IfH6A479qlmJqSLFVB2l6',
    'COMUM'
);

-- FILMES (admin = id_usuario_admin = 1)
INSERT INTO filme (titulo, categoria, ano, poster_url, id_usuario_admin)
VALUES
  ('Shrek', 'Animação', 2001,
   'https://i.ebayimg.com/images/g/4voAAOSw41VnKpeY/s-l1200.jpg', 1),

  ('O Senhor dos Anéis: A Sociedade do Anel', 'Fantasia', 2001,
   'https://m.media-amazon.com/images/I/51Qvs9i5a%2BL._AC_UF894,1000_QL80_.jpg', 1),

  ('Homem-Aranha 2', 'Ação', 2004,
   'https://br.web.img2.acsta.net/r_1920_1080/pictures/210/544/21054460_2013103118041242.jpg', 1),

  ('Matrix', 'Ficção Científica', 1999,
   'https://m.media-amazon.com/images/I/51EG732BV3L._AC_UF894,1000_QL80_.jpg', 1),

  ('Toy Story', 'Animação', 1995,
   'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSbJV0sn8ZJ1rC7qro7hCyL-xogBq6ryT7UsrVjtPgIi-_FnDWHDIfe5pmY9JnGNhnM27KWMEmm0AReOVZIJBKNRL425SmWmzBqqbO9qnl1&s=10', 1),

  ('Vingadores: Ultimato', 'Ação', 2019,
   'https://play-lh.googleusercontent.com/nz5s7-HJ3iijnvitzCwsa_oD5l7saKAnF_Hv3qGtsP6dtsXTqESZmfuQKqyZZD4UywyB', 1),

  ('Batman: O Cavaleiro das Trevas', 'Ação', 2008,
   'https://m.media-amazon.com/images/I/61nB9sgdFeL._AC_UF894,1000_QL80_.jpg', 1),

  ('Interestelar', 'Ficção Científica', 2014,
   'https://images.justwatch.com/poster/312160153/s718/interestelar.jpg', 1),

  ('Divertida Mente', 'Animação', 2015,
   'https://cdn.awsli.com.br/800x800/1576/1576093/produto/305842625/divertida-mente-2-hq_capa--baixa--ma1sa1f8om.jpg', 1),

  ('Cidade de Deus', 'Drama', 2002,
   'https://upload.wikimedia.org/wikipedia/pt/1/10/CidadedeDeus.jpg', 1),

  ('Star Wars: Episódio IV - Uma Nova Esperança', 'Ficção Científica', 1977,
   'https://m.media-amazon.com/images/M/MV5BOWE0MDA0YjgtOGVhMi00NjY0LTkxNGQtYjVjMWU3ZTU5Mjc1XkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg', 1),

  ('Procurando Nemo', 'Animação', 2003,
   'https://upload.wikimedia.org/wikipedia/pt/thumb/2/29/Finding_Nemo.jpg/250px-Finding_Nemo.jpg', 1);
