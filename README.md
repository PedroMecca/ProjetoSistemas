======================================================================
                          🎬  MOVIEHUB  
               Sistema de Avaliação e Comentários de Filmes
======================================================================

Aplicação completa (backend + frontend) que permite:
- Criar conta e fazer login (com JWT)
- Listar filmes e visualizar notas
- Avaliar filmes com nota de 0 a 5
- Adicionar comentários
- Ver avaliações e comentários de outros usuários
- Área especial para ADMIN cadastrar novos filmes

Tecnologias:
- Backend: Java 17, Spring Boot, Spring Security, JWT, H2, Flyway
- Frontend: React + Vite + Node.js, CSS puro

Desenvolvido por: PEDRO BONELLI
======================================================================



======================================================================
                   📁  ESTRUTURA DO PROJETO (BACKEND)
======================================================================

src/main/java/com/example/demo
│
├── config       → Configurações gerais (CORS, Beans...)
├── controller   → Endpoints REST da aplicação
├── dto          → Objetos de entrada e saída (requests/responses)
├── model        → Entidades JPA (Usuario, Filme, Avaliacao, Comentario...)
├── repository   → Interfaces JPA para acessar o banco
└── security     → JWT, filtros, UserDetails, autenticação

Migrações do banco (Flyway):
src/main/resources/db/migration
- V1__create_tables.sql
- V2__insertions.sql
…

======================================================================



======================================================================
                    ▶️ COMO SUBIR O BACKEND (API)
======================================================================

PRÉ-REQUISITOS:
- Java 17+
- Maven instalado

1. Entre na pasta do backend:
   cd backend

2. Execute:
   mvn spring-boot:run

OU rode a aplicação pela IDE (IntelliJ):
- Abra o projeto
- Execute a classe principal que contém @SpringBootApplication

A API iniciará em:
   http://localhost:8080

O Flyway criará todas as tabelas e populará usuários + filmes iniciais.


======================================================================



======================================================================
                👤 USUÁRIOS DE TESTE (CRIADOS AUTOMATICAMENTE)
======================================================================

ADMIN:
   Email: admin@gmail.com
   Senha: @123

USUÁRIO COMUM:
   Email: usuario@gmail.com
   Senha: @124

======================================================================



======================================================================
                    🌐 ENDPOINTS PRINCIPAIS DA API
======================================================================

AUTENTICAÇÃO:
- POST /login
  Retorno:
      { "token": "JWT_AQUI" }

USUÁRIOS:
- POST /usuarios/registro
- GET  /usuarios/me

FILMES:
- GET  /filmes
- POST /filmes            (ADMIN)

AVALIAÇÕES:
- GET  /filmes/{id}/avaliacoes
- POST /filmes/{id}/avaliacoes
      { "nota": 4.5 }

COMENTÁRIOS:
- GET  /filmes/{id}/comentarios
- POST /filmes/{id}/comentarios
      { "texto": "Excelente filme!" }

======================================================================



======================================================================
                💻 COMO RODAR O FRONTEND (REACT + VITE)
======================================================================

PRÉ-REQUISITOS:
- Node.js 18 ou superior
- npm instalado

1. Entre na pasta do frontend:
   cd frontend

2. Instale dependências:
   npm install

3. Inicie o servidor de desenvolvimento:
   npm run dev

O frontend normalmente sobe em:
   http://localhost:5173

O backend deve estar rodando simultaneamente em:
   http://localhost:8080

======================================================================



======================================================================
                    🔄 FLUXO DE USO DO SISTEMA
======================================================================

1) Acesse o frontend (http://localhost:5173)
2) Crie uma conta ou use os usuários de teste
3) Faça login → token é salvo no navegador
4) Veja os filmes cadastrados
5) Avalie filmes ou comente
6) Clique em "Ver comentários" para abrir o painel
7) Admins podem cadastrar novos filmes

======================================================================



======================================================================
                 🚀 POSSÍVEIS MELHORIAS FUTURAS
======================================================================

- Sistema de favoritos
- Paginação de filmes
- Upload real de pôster (imagem)
- Tela dedicada para detalhes do filme
- Remoção/edição de filmes pelo ADMIN

======================================================================
                 Projeto desenvolvido por Pedro Bonelli
======================================================================
