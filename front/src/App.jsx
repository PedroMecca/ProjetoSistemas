import { useEffect, useState, useRef } from "react";
import "./App.css";

const API_BASE = "http://localhost:8080";

function App() {
  const [mode, setMode] = useState("login"); // "login" | "register"

  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [tipoUsuario] = useState("COMUM"); // cadastro público sempre COMUM

  // ref para scroll até a área de comentários
  const comentariosRef = useRef(null);

  const [token, setToken] = useState(localStorage.getItem("token") || null);
  const [userEmail, setUserEmail] = useState(
    localStorage.getItem("userEmail") || null
  );
  const [userName, setUserName] = useState(
    localStorage.getItem("userName") || null
  );
  const [userRole, setUserRole] = useState(
    localStorage.getItem("userRole") || null
  ); // "ADMIN" | "COMUM"

  const [filmes, setFilmes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [mensagem, setMensagem] = useState("");

  // filtro: título + categoria (chips)
  const [filtroTitulo, setFiltroTitulo] = useState("");
  const [categoriaSelecionada, setCategoriaSelecionada] = useState("ALL");

  // admin: cadastro de novo filme
  const [novoTitulo, setNovoTitulo] = useState("");
  const [novoCategoria, setNovoCategoria] = useState("");
  const [novoAno, setNovoAno] = useState("");
  const [novoPosterUrl, setNovoPosterUrl] = useState("");

  // painel de avaliações/comentários (admin e usuário comum)
  const [filmeSelecionado, setFilmeSelecionado] = useState(null);
  const [avaliacoes, setAvaliacoes] = useState([]);
  const [comentarios, setComentarios] = useState([]);

  // modais (avaliar + comentar)
  const [avaliacaoModalAberto, setAvaliacaoModalAberto] = useState(false);
  const [comentarioModalAberto, setComentarioModalAberto] = useState(false);
  const [filmeEmEdicao, setFilmeEmEdicao] = useState(null);
  const [notaInput, setNotaInput] = useState("");
  const [comentarioInput, setComentarioInput] = useState("");

  // ----------------------------------------------------
  // Helpers
  // ----------------------------------------------------

  async function carregarPerfil(tokenParam) {
    const jwt = tokenParam || token;
    if (!jwt) return;

    try {
      const res = await fetch(`${API_BASE}/usuarios/me`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });

      if (!res.ok) {
        throw new Error("Não foi possível carregar o perfil.");
      }

      const data = await res.json();
      setUserEmail(data.email);
      setUserName(data.nome);
      setUserRole(data.tipoUsuario);

      localStorage.setItem("userEmail", data.email);
      localStorage.setItem("userName", data.nome);
      localStorage.setItem("userRole", data.tipoUsuario);
    } catch (err) {
      console.error("Erro ao carregar perfil:", err);
    }
  }

  async function carregarFilmes() {
    if (!token) return;

    try {
      setLoading(true);
      setMensagem("");

      const res = await fetch(`${API_BASE}/filmes`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!res.ok) {
        throw new Error("Falha ao carregar filmes");
      }

      const data = await res.json();
      setFilmes(data);
    } catch (err) {
      console.error(err);
      setMensagem("Erro ao carregar filmes.");
    } finally {
      setLoading(false);
    }
  }

  // carrega avaliações (notas) + comentários em um único painel
  async function carregarAvaliacoesEComentarios(filme) {
    if (!token) return;

    try {
      setLoading(true);
      setMensagem("");
      setFilmeSelecionado(filme);
      setAvaliacoes([]);
      setComentarios([]);

      const [resA, resC] = await Promise.all([
        fetch(`${API_BASE}/filmes/${filme.id}/avaliacoes`, {
          headers: { Authorization: `Bearer ${token}` },
        }),
        fetch(`${API_BASE}/filmes/${filme.id}/comentarios`, {
          headers: { Authorization: `Bearer ${token}` },
        }),
      ]);

      if (!resA.ok || !resC.ok) {
        throw new Error("Falha ao carregar avaliações/comentários");
      }

      const dataA = await resA.json();
      const dataC = await resC.json();

      setAvaliacoes(dataA);
      setComentarios(dataC);

      // scroll até a área de comentários
      setTimeout(() => {
        if (comentariosRef.current) {
          comentariosRef.current.scrollIntoView({ behavior: "smooth" });
        }
      }, 150);
    } catch (err) {
      console.error(err);
      setMensagem("Erro ao carregar comentários do filme.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (token) {
      carregarPerfil(token);
      carregarFilmes();
    }
  }, [token]);

  // ----------------------------------------------------
  // Auth: login / registro
  // ----------------------------------------------------
  async function handleLogin(e) {
    e.preventDefault();
    setMensagem("");
    setLoading(true);

    try {
      const res = await fetch(`${API_BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, senha }),
      });

      if (!res.ok) {
        throw new Error("Credenciais inválidas");
      }

      const data = await res.json(); // { token: "..." }
      setToken(data.token);
      localStorage.setItem("token", data.token);

      await carregarPerfil(data.token);

      setMensagem("Login realizado com sucesso!");
    } catch (err) {
      console.error(err);
      setMensagem("Erro ao fazer login. Verifique e-mail/senha.");
    } finally {
      setLoading(false);
    }
  }

  async function handleRegister(e) {
    e.preventDefault();
    setMensagem("");
    setLoading(true);

    if (senha.length < 6) {
      setMensagem("Senha deve ter pelo menos 6 caracteres.");
      setLoading(false);
      return;
    }

    try {
      // 1) cria o usuário
      const res = await fetch(`${API_BASE}/usuarios/registro`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nome, email, senha, tipoUsuario }),
      });

      if (!res.ok) {
        const errorText = await res.text();
        console.error("Erro ao registrar usuário:", res.status, errorText);

        let msg = "Erro ao registrar usuário.";
        try {
          const obj = JSON.parse(errorText);
          msg = obj.message || msg;
        } catch (_) {
          msg = errorText;
        }

        throw new Error(msg);
      }

      setMensagem("Conta criada! Fazendo login...");

      // 2) login automático
      const loginRes = await fetch(`${API_BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, senha }),
      });

      if (!loginRes.ok) {
        throw new Error("Conta criada, mas erro ao logar.");
      }

      const loginData = await loginRes.json();
      setToken(loginData.token);
      localStorage.setItem("token", loginData.token);

      await carregarPerfil(loginData.token);

      setMensagem("Conta criada e login realizado!");
    } catch (err) {
      console.error(err);
      setMensagem(err.message || "Erro ao criar conta. Tente outro e-mail.");
    } finally {
      setLoading(false);
    }
  }

  function handleLogout() {
    setToken(null);
    setUserEmail(null);
    setUserName(null);
    setUserRole(null);
    setFilmes([]);
    setAvaliacoes([]);
    setComentarios([]);
    setFilmeSelecionado(null);
    localStorage.removeItem("token");
    localStorage.removeItem("userEmail");
    localStorage.removeItem("userName");
    localStorage.removeItem("userRole");
    setMensagem("Você saiu da conta.");
  }

  // ----------------------------------------------------
  // Modais: abrir/fechar
  // ----------------------------------------------------
  function abrirModalAvaliacao(filme) {
    setFilmeEmEdicao(filme);
    setNotaInput("");
    setAvaliacaoModalAberto(true);
  }

  function abrirModalComentario(filme) {
    setFilmeEmEdicao(filme);
    setComentarioInput("");
    setComentarioModalAberto(true);
  }

  function fecharModais() {
    setAvaliacaoModalAberto(false);
    setComentarioModalAberto(false);
    setFilmeEmEdicao(null);
    setNotaInput("");
    setComentarioInput("");
  }

  // ----------------------------------------------------
  // Ações de filme (enviar avaliação/comentário via modal)
  // ----------------------------------------------------
  async function handleAvaliarSubmit(e) {
    e.preventDefault();
    if (!filmeEmEdicao) return;

    const nota = Number(notaInput);
    if (Number.isNaN(nota) || nota < 0 || nota > 5) {
      setMensagem("A nota deve ser um número entre 0 e 5.");
      return;
    }

    try {
      setLoading(true);
      setMensagem("");

      const res = await fetch(
        `${API_BASE}/filmes/${filmeEmEdicao.id}/avaliacoes`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ nota }),
        }
      );

      if (!res.ok) {
        throw new Error("Erro ao enviar avaliação");
      }

      await res.json();
      setMensagem("Avaliação enviada/atualizada com sucesso!");

      await carregarFilmes();

      if (filmeSelecionado && filmeSelecionado.id === filmeEmEdicao.id) {
        await carregarAvaliacoesEComentarios(filmeSelecionado);
      }

      fecharModais();
    } catch (err) {
      console.error(err);
      setMensagem("Erro ao avaliar o filme.");
    } finally {
      setLoading(false);
    }
  }

  async function handleComentarSubmit(e) {
    e.preventDefault();
    if (!filmeEmEdicao) return;

    const texto = comentarioInput.trim();
    if (!texto) {
      setMensagem("Digite um comentário antes de enviar.");
      return;
    }

    try {
      setLoading(true);
      setMensagem("");

      const res = await fetch(
        `${API_BASE}/filmes/${filmeEmEdicao.id}/comentarios`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ texto }),
        }
      );

      if (!res.ok) {
        throw new Error("Erro ao adicionar comentário");
      }

      await res.json();
      setMensagem("Comentário adicionado com sucesso!");

      if (filmeSelecionado && filmeSelecionado.id === filmeEmEdicao.id) {
        await carregarAvaliacoesEComentarios(filmeSelecionado);
      }

      fecharModais();
    } catch (err) {
      console.error(err);
      setMensagem("Erro ao adicionar comentário.");
    } finally {
      setLoading(false);
    }
  }

  async function handleCriarFilme(e) {
    e.preventDefault();
    if (!novoTitulo.trim()) {
      alert("Informe o título do filme.");
      return;
    }

    try {
      setLoading(true);
      setMensagem("");

      const body = {
        titulo: novoTitulo,
        categoria: novoCategoria,
        ano: novoAno ? Number(novoAno) : null,
        posterUrl: novoPosterUrl || null,
      };

      const res = await fetch(`${API_BASE}/filmes`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(body),
      });

      if (!res.ok) {
        throw new Error("Erro ao cadastrar filme.");
      }

      await res.json();
      setMensagem("Filme cadastrado com sucesso!");

      setNovoTitulo("");
      setNovoCategoria("");
      setNovoAno("");
      setNovoPosterUrl("");

      carregarFilmes();
    } catch (err) {
      console.error(err);
      setMensagem("Erro ao cadastrar filme.");
    } finally {
      setLoading(false);
    }
  }

  // ----------------------------------------------------
  // Filtro aplicado (título + categoria)
  // ----------------------------------------------------
  const filmesFiltrados = filmes.filter((f) => {
    const termoTitulo = filtroTitulo.toLowerCase();
    const titulo = (f.titulo || "").toLowerCase();
    const categoria = (f.categoria || "").toLowerCase();

    const matchTitulo = !termoTitulo || titulo.includes(termoTitulo);
    const matchCategoria =
      categoriaSelecionada === "ALL" ||
      categoria === categoriaSelecionada.toLowerCase();

    return matchTitulo && matchCategoria;
  });

  // ----------------------------------------------------
  // Tela de login / cadastro
  // ----------------------------------------------------
  if (!token) {
    return (
      <div className="app-container">
        <div className="card">
          <h1 className="title">
            Movie<span className="highlight">Hub</span> 🎬
          </h1>
          <p className="subtitle">Seja bem-vindo!</p>
          <p className="subtitle small">
            Desenvolvido por <strong>Pedro Bonelli</strong>
          </p>
          <p className="subtitle">
            {mode === "login"
              ? "Entre com sua conta para ver e avaliar filmes."
              : "Crie sua conta para começar a avaliar filmes."}
          </p>

          <form
            className="form"
            onSubmit={mode === "login" ? handleLogin : handleRegister}
          >
            {mode === "register" && (
              <div className="form-group">
                <label>Nome</label>
                <input
                  type="text"
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  required
                  placeholder="Seu nome"
                />
              </div>
            )}

            <div className="form-group">
              <label>E-mail</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                placeholder="seuemail@gmail.com"
              />
            </div>

            <div className="form-group">
              <label>Senha</label>
              <input
                type="password"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                required
                placeholder="••••••••"
              />
            </div>

            <button className="btn primary" type="submit" disabled={loading}>
              {loading
                ? "Enviando..."
                : mode === "login"
                ? "Entrar"
                : "Criar conta"}
            </button>
          </form>

          <button
            className="btn link"
            onClick={() => {
              setMode(mode === "login" ? "register" : "login");
              setMensagem("");
            }}
          >
            {mode === "login"
              ? "Não tem conta? Criar conta"
              : "Já tem conta? Fazer login"}
          </button>

          {mensagem && <p className="message">{mensagem}</p>}

          <p className="footer-text">
            Backend: <code>http://localhost:8080</code> | Front:{" "}
            <code>http://localhost:5173</code>
          </p>
        </div>
      </div>
    );
  }

  const isAdmin = userRole === "ADMIN";

  // ----------------------------------------------------
  // Tela ADMIN
  // ----------------------------------------------------
  if (isAdmin) {
    return (
      <>
        {/* Modal de avaliação (admin) */}
        {avaliacaoModalAberto && (
          <div className="modal-backdrop">
            <div className="modal">
              <h2 className="modal-title">Avaliar filme</h2>
              <p className="modal-subtitle">
                Filme: <strong>{filmeEmEdicao?.titulo}</strong>
              </p>

              <form onSubmit={handleAvaliarSubmit} className="form">
                <div className="form-group">
                  <label>Nota (0 a 5)</label>
                  <input
                    type="number"
                    min="0"
                    max="5"
                    step="0.5"
                    value={notaInput}
                    onChange={(e) => setNotaInput(e.target.value)}
                    required
                  />
                </div>
                <div className="modal-actions">
                  <button
                    type="button"
                    className="btn secondary"
                    onClick={fecharModais}
                  >
                    Cancelar
                  </button>
                  <button className="btn primary" type="submit" disabled={loading}>
                    {loading ? "Enviando..." : "Salvar avaliação"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        <div className="app-container">
          <div className="card wide">
            <header className="top-bar">
              <div>
                <h1 className="title">
                  Movie<span className="highlight">Hub</span> 🎬
                </h1>
                <p className="subtitle">
                  Seja bem-vindo, <strong>{userName || userEmail}</strong>
                </p>
                <p className="subtitle small">
                  Desenvolvido por <strong>Pedro Bonelli</strong>
                </p>
              </div>
              <button className="btn secondary" onClick={handleLogout}>
                Sair
              </button>
            </header>

            <div className="dashboard">
              <div className="dashboard-main">
                <section className="section">
                  <h2 className="section-title">Cadastrar novo filme</h2>
                  <form className="form form-inline" onSubmit={handleCriarFilme}>
                    <div className="form-group">
                      <label>Título</label>
                      <input
                        type="text"
                        value={novoTitulo}
                        onChange={(e) => setNovoTitulo(e.target.value)}
                        required
                        placeholder="Nome do filme"
                      />
                    </div>
                    <div className="form-group">
                      <label>Categoria</label>
                      <input
                        type="text"
                        value={novoCategoria}
                        onChange={(e) => setNovoCategoria(e.target.value)}
                        placeholder="Ação, Suspense..."
                      />
                    </div>
                    <div className="form-group">
                      <label>Ano</label>
                      <input
                        type="number"
                        value={novoAno}
                        onChange={(e) => setNovoAno(e.target.value)}
                        placeholder="2025"
                      />
                    </div>
                    <div className="form-group">
                      <label>URL da imagem (pôster/logo)</label>
                      <input
                        type="url"
                        value={novoPosterUrl}
                        onChange={(e) => setNovoPosterUrl(e.target.value)}
                        placeholder="https://exemplo.com/poster.jpg"
                      />
                    </div>
                    <button
                      className="btn primary"
                      type="submit"
                      disabled={loading}
                    >
                      {loading ? "Salvando..." : "Cadastrar filme"}
                    </button>
                  </form>
                </section>

                <section className="section">
                  <div className="actions">
                    <div className="filter-group">
                      <span className="filter-label">Buscar por</span>

                      <input
                        className="search-input"
                        type="text"
                        value={filtroTitulo}
                        onChange={(e) => setFiltroTitulo(e.target.value)}
                        placeholder="Título do filme..."
                      />

                      <div className="category-chips">
                        {[
                          { label: "Todas", value: "ALL" },
                          { label: "Animação", value: "Animação" },
                          { label: "Ação", value: "Ação" },
                          { label: "Fantasia", value: "Fantasia" },
                          { label: "Ficção Científica", value: "ficção científica" },
                          { label: "Drama", value: "drama" },
                        ].map((cat) => (
                          <button
                            key={cat.value}
                            type="button"
                            className={
                              "chip" +
                              (categoriaSelecionada === cat.value ? " active" : "")
                            }
                            onClick={() => setCategoriaSelecionada(cat.value)}
                          >
                            {cat.label}
                          </button>
                        ))}
                      </div>
                    </div>

                    <button className="btn primary" onClick={carregarFilmes}>
                      Atualizar lista
                    </button>
                    {loading && <span className="loading">Carregando...</span>}
                  </div>

                  {mensagem && <p className="message">{mensagem}</p>}

                  <div className="films-grid">
                    {filmesFiltrados.length === 0 && !loading && (
                      <p>Nenhum filme cadastrado ainda.</p>
                    )}

                    {filmesFiltrados.map((filme) => (
                      <div key={filme.id} className="film-card">
                        {filme.posterUrl && (
                          <div className="film-poster">
                            <img
                              src={filme.posterUrl}
                              alt={`Pôster de ${filme.titulo}`}
                            />
                          </div>
                        )}

                        <h2>{filme.titulo}</h2>
                        <p className="film-meta">
                          Categoria:{" "}
                          <strong>{filme.categoria || "Sem categoria"}</strong>
                        </p>
                        <p className="film-meta">
                          Ano: <strong>{filme.ano || "—"}</strong>
                        </p>
                        <p className="film-meta">
                          Média de avaliação:{" "}
                          <strong>
                            {filme.mediaAvaliacao != null
                              ? filme.mediaAvaliacao.toFixed(1)
                              : "N/A"}
                          </strong>
                        </p>
                        <p className="film-meta">
                          Criado por: <strong>{filme.adminNome}</strong> (
                          {filme.adminEmail})
                        </p>

                        <div className="film-actions">
                          <button
                            className="btn small primary"
                            onClick={() => abrirModalAvaliacao(filme)}
                          >
                            Avaliar filme
                          </button>
                          <button
                            className="btn small secondary"
                            onClick={() =>
                              carregarAvaliacoesEComentarios(filme)
                            }
                          >
                            Ver comentários
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                </section>
              </div>

              <aside
                className="dashboard-side"
                ref={comentariosRef} // scroll aqui no admin
              >
                <h2 className="section-title">Comentários do filme</h2>
                {!filmeSelecionado && (
                  <p className="film-meta">
                    Selecione um filme em &quot;Ver comentários&quot;.
                  </p>
                )}

                {filmeSelecionado && (
                  <>
                    <p className="film-meta">
                      Filme: <strong>{filmeSelecionado.titulo}</strong>
                    </p>
                    <div className="avaliacoes-list">
                      {/* Avaliações com nota */}
                      {avaliacoes.map((av) => (
                        <div key={`av-${av.id}`} className="avaliacao-card">
                          {av.comentario && (
                            <p className="avaliacao-comentario">
                              {av.comentario}
                            </p>
                          )}
                          <p className="avaliacao-nota">
                            Nota: <strong>{av.nota}</strong> ⭐
                          </p>
                          <p className="avaliacao-meta">
                            Por:{" "}
                            <strong>
                              {av.usuarioComum?.nome || av.usuarioComum?.email}
                            </strong>{" "}
                            em {av.dataAvaliacao}
                          </p>
                        </div>
                      ))}

                      {/* Comentários sem nota */}
                      {comentarios.map((c) => (
                        <div key={`c-${c.id}`} className="avaliacao-card">
                          <p className="avaliacao-comentario">{c.texto}</p>
                          <p className="avaliacao-meta">
                            Por:{" "}
                            <strong>
                              {c.usuarioComum?.nome || c.usuarioComum?.email}
                            </strong>{" "}
                            em {c.dataComentario}
                          </p>
                        </div>
                      ))}

                      {avaliacoes.length === 0 &&
                        comentarios.length === 0 &&
                        !loading && (
                          <p className="film-meta">
                            Nenhuma avaliação/comentário registrado ainda.
                          </p>
                        )}
                    </div>
                  </>
                )}
              </aside>
            </div>
          </div>
        </div>
      </>
    );
  }

  // ----------------------------------------------------
  // Tela USUÁRIO COMUM
  // ----------------------------------------------------
  return (
    <>
      {/* Modal de avaliação */}
      {avaliacaoModalAberto && (
        <div className="modal-backdrop">
          <div className="modal">
            <h2 className="modal-title">Avaliar filme</h2>
            <p className="modal-subtitle">
              Filme: <strong>{filmeEmEdicao?.titulo}</strong>
            </p>

            <form onSubmit={handleAvaliarSubmit} className="form">
              <div className="form-group">
                <label>Nota (0 a 5)</label>
                <input
                  type="number"
                  min="0"
                  max="5"
                  step="0.5"
                  value={notaInput}
                  onChange={(e) => setNotaInput(e.target.value)}
                  required
                />
              </div>
              <div className="modal-actions">
                <button
                  type="button"
                  className="btn secondary"
                  onClick={fecharModais}
                >
                  Cancelar
                </button>
                <button className="btn primary" type="submit" disabled={loading}>
                  {loading ? "Enviando..." : "Salvar avaliação"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal de comentário */}
      {comentarioModalAberto && (
        <div className="modal-backdrop">
          <div className="modal">
            <h2 className="modal-title">Adicionar comentário</h2>
            <p className="modal-subtitle">
              Filme: <strong>{filmeEmEdicao?.titulo}</strong>
            </p>

            <form onSubmit={handleComentarSubmit} className="form">
              <div className="form-group">
                <label>Comentário</label>
                <textarea
                  className="textarea"
                  rows={3}
                  value={comentarioInput}
                  onChange={(e) => setComentarioInput(e.target.value)}
                  required
                />
              </div>
              <div className="modal-actions">
                <button
                  type="button"
                  className="btn secondary"
                  onClick={fecharModais}
                >
                  Cancelar
                </button>
                <button className="btn primary" type="submit" disabled={loading}>
                  {loading ? "Enviando..." : "Enviar comentário"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="app-container">
        <div className="card wide">
          <header className="top-bar">
            <div>
              <h1 className="title">
                Movie<span className="highlight">Hub</span> 🎬
              </h1>
              <p className="subtitle">
                Seja bem-vindo, <strong>{userName || userEmail}</strong>
              </p>

              <p className="subtitle small">
                Desenvolvido por <strong>Pedro Bonelli</strong>
              </p>
            </div>
            <button className="btn secondary" onClick={handleLogout}>
              Sair
            </button>
          </header>

          <section className="section">
            <div className="actions">
              <div className="filter-group">
                <span className="filter-label">Buscar por</span>

                <input
                  className="search-input"
                  type="text"
                  value={filtroTitulo}
                  onChange={(e) => setFiltroTitulo(e.target.value)}
                  placeholder="Título do filme..."
                />

                <div className="category-chips">
                  {[
                    { label: "Todas", value: "ALL" },
                    { label: "Animação", value: "Animação" },
                    { label: "Ação", value: "Ação" },
                    { label: "Fantasia", value: "Fantasia" },
                    { label: "Ficção Científica", value: "ficção científica" },
                    { label: "Drama", value: "drama" },
                  ].map((cat) => (
                    <button
                      key={cat.value}
                      type="button"
                      className={
                        "chip" +
                        (categoriaSelecionada === cat.value ? " active" : "")
                      }
                      onClick={() => setCategoriaSelecionada(cat.value)}
                    >
                      {cat.label}
                    </button>
                  ))}
                </div>
              </div>

              <button className="btn primary" onClick={carregarFilmes}>
                Atualizar lista
              </button>
              {loading && <span className="loading">Carregando...</span>}
            </div>

            {mensagem && <p className="message">{mensagem}</p>}

            <div className="films-grid">
              {filmesFiltrados.length === 0 && !loading && (
                <p>Nenhum filme cadastrado ainda.</p>
              )}

              {filmesFiltrados.map((filme) => (
                <div key={filme.id} className="film-card">
                  {filme.posterUrl && (
                    <div className="film-poster">
                      <img
                        src={filme.posterUrl}
                        alt={`Pôster de ${filme.titulo}`}
                      />
                    </div>
                  )}

                  <h2>{filme.titulo}</h2>
                  <p className="film-meta">
                    Categoria:{" "}
                    <strong>{filme.categoria || "Sem categoria"}</strong>
                  </p>
                  <p className="film-meta">
                    Ano: <strong>{filme.ano || "—"}</strong>
                  </p>
                  <p className="film-meta">
                    Média de avaliação:{" "}
                    <strong>
                      {filme.mediaAvaliacao != null
                        ? filme.mediaAvaliacao.toFixed(1)
                        : "N/A"}
                    </strong>
                  </p>

                  <div className="film-actions">
                    <button
                      className="btn small primary"
                      onClick={() => abrirModalAvaliacao(filme)}
                    >
                      Avaliar filme
                    </button>

                    <button
                      className="btn small secondary"
                      onClick={() => abrirModalComentario(filme)}
                    >
                      Adicionar comentário
                    </button>

                    <button
                      className="btn small secondary"
                      onClick={() => carregarAvaliacoesEComentarios(filme)}
                    >
                      Ver comentários
                    </button>
                  </div>
                </div>
              ))}
            </div>

            {/* Painel de comentários para usuário comum */}
            <section
              className="section comments-section"
              ref={comentariosRef} // scroll aqui no usuário comum
            >
              <h2 className="section-title">Comentários do filme</h2>

              {!filmeSelecionado && (
                <p className="film-meta">
                  Clique em <strong>"Ver comentários"</strong> em algum filme
                  para ver as avaliações e comentários.
                </p>
              )}

              {filmeSelecionado && (
                <>
                  <p className="film-meta">
                    Filme: <strong>{filmeSelecionado.titulo}</strong>
                  </p>

                  <div className="avaliacoes-list">
                    {/* Avaliações com nota (podem ter comentário) */}
                    {avaliacoes.map((av) => (
                      <div key={`av-user-${av.id}`} className="avaliacao-card">
                        {av.comentario && (
                          <p className="avaliacao-comentario">
                            {av.comentario}
                          </p>
                        )}
                        <p className="avaliacao-nota">
                          Nota: <strong>{av.nota}</strong> ⭐
                        </p>
                        <p className="avaliacao-meta">
                          Por:{" "}
                          <strong>
                            {av.usuarioComum?.nome || av.usuarioComum?.email}
                          </strong>{" "}
                          em {av.dataAvaliacao}
                        </p>
                      </div>
                    ))}

                    {/* Comentários sem nota */}
                    {comentarios.map((c) => (
                      <div key={`c-user-${c.id}`} className="avaliacao-card">
                        <p className="avaliacao-comentario">{c.texto}</p>
                        <p className="avaliacao-meta">
                          Por:{" "}
                          <strong>
                            {c.usuarioComum?.nome || c.usuarioComum?.email}
                          </strong>{" "}
                          em {c.dataComentario}
                        </p>
                      </div>
                    ))}

                    {avaliacoes.length === 0 &&
                      comentarios.length === 0 &&
                      !loading && (
                        <p className="film-meta">
                          Nenhuma avaliação/comentário registrado ainda para
                          este filme.
                        </p>
                      )}
                  </div>
                </>
              )}
            </section>
          </section>
        </div>
      </div>
    </>
  );
}

export default App;
