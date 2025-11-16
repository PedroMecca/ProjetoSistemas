import { useEffect, useState } from "react";
import "./App.css";

const API_BASE = "http://localhost:8080";

function App() {
  const [mode, setMode] = useState("login"); // "login" | "register"
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [tipoUsuario] = useState("COMUM"); // vamos criar sempre usuário comum

  const [token, setToken] = useState(localStorage.getItem("token") || null);
  const [userEmail, setUserEmail] = useState(localStorage.getItem("userEmail") || null);

  const [filmes, setFilmes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [mensagem, setMensagem] = useState("");

  // Se tiver token, busca filmes automaticamente
  useEffect(() => {
    if (token) {
      carregarFilmes();
    }
  }, [token]);

  async function carregarFilmes() {
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
      setUserEmail(email);

      localStorage.setItem("token", data.token);
      localStorage.setItem("userEmail", email);

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

    try {
      // 1) cria o usuário
      const res = await fetch(`${API_BASE}/usuarios/registro`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nome, email, senha, tipoUsuario }),
      });

      // resp.ok é true para qualquer 2xx (200, 201, 204...)
      if (!res.ok) {
        if (res.status === 400) {
          // por exemplo: e-mail já cadastrado
          throw new Error("E-mail já cadastrado.");
        }
        throw new Error("Erro ao registrar usuário.");
      }

      // se quiser, pode ler o usuário criado:
      // const userCreated = await res.json();
      // console.log("Usuário criado:", userCreated);

      setMensagem("Conta criada! Fazendo login...");

      // 2) faz login automaticamente
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
      setUserEmail(email);

      localStorage.setItem("token", loginData.token);
      localStorage.setItem("userEmail", email);

      setMensagem("Conta criada e login realizado!");
      setMode("login"); // aqui você decide: manter "login" ou já deixar logado
    } catch (err) {
      console.error(err);
      // se a mensagem for mais específica, usa ela;
      // se não, usa o texto padrão
      setMensagem(err.message || "Erro ao criar conta. Tente outro e-mail.");
    } finally {
      setLoading(false);
    }
  }


  function handleLogout() {
    setToken(null);
    setUserEmail(null);
    setFilmes([]);
    localStorage.removeItem("token");
    localStorage.removeItem("userEmail");
    setMensagem("Você saiu da conta.");
  }

  async function handleAvaliar(filmeId) {
    const notaStr = prompt("Informe a nota (0 a 5):");
    if (notaStr === null) return;

    const nota = Number(notaStr);
    if (Number.isNaN(nota) || nota < 0 || nota > 5) {
      alert("Nota inválida. Digite um número de 0 a 5.");
      return;
    }

    const comentario = prompt("Comentário sobre o filme:") || "";

    try {
      setLoading(true);
      setMensagem("");

      const res = await fetch(`${API_BASE}/filmes/${filmeId}/avaliacoes`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ nota, comentario }),
      });

      if (!res.ok) {
        throw new Error("Erro ao enviar avaliação");
      }

      const data = await res.json();
      console.log("Avaliação salva:", data);
      setMensagem("Avaliação enviada com sucesso!");
    } catch (err) {
      console.error(err);
      setMensagem("Erro ao avaliar o filme.");
    } finally {
      setLoading(false);
    }
  }

  // Se não tem token → mostra tela de login/cadastro
  if (!token) {
    return (
      <div className="app-container">
        <div className="card">
          <h1 className="title">Avaliador de Filmes 🎬</h1>
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

  // Se tem token → mostra tela de filmes
  return (
    <div className="app-container">
      <div className="card wide">
        <header className="top-bar">
          <div>
            <h1 className="title">Filmes 🎥</h1>
            <p className="subtitle">
              Logado como <strong>{userEmail}</strong>
            </p>
          </div>
          <button className="btn secondary" onClick={handleLogout}>
            Sair
          </button>
        </header>

        <div className="actions">
          <button className="btn primary" onClick={carregarFilmes}>
            Atualizar lista
          </button>
          {loading && <span className="loading">Carregando...</span>}
        </div>

        {mensagem && <p className="message">{mensagem}</p>}

        <div className="films-grid">
          {filmes.length === 0 && !loading && (
            <p>Nenhum filme cadastrado ainda.</p>
          )}

          {filmes.map((filme) => (
            <div key={filme.id} className="film-card">
              <h2>{filme.titulo}</h2>
              <p className="film-meta">
                Criado por: <strong>{filme.adminNome}</strong> (
                {filme.adminEmail})
              </p>
              <p className="film-tag">{filme.tipoUsuario}</p>

              <button
                className="btn small primary"
                onClick={() => handleAvaliar(filme.id)}
              >
                Avaliar filme
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default App;
