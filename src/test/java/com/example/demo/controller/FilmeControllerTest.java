package com.example.demo.controller;

import com.example.demo.dto.AvaliacaoRequest;
import com.example.demo.dto.AvaliacaoResponse;
import com.example.demo.dto.FilmeRequest;
import com.example.demo.dto.FilmeResponse;
import com.example.demo.model.Avaliacao;
import com.example.demo.model.Favorito;
import com.example.demo.model.Filme;
import com.example.demo.model.TipoUsuario;
import com.example.demo.model.Usuario;
import com.example.demo.repository.AvaliacaoRepository;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.FilmeRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.UsuarioDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FilmeController.class)
@AutoConfigureMockMvc(addFilters = false)
class FilmeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmeRepository filmeRepo;

    @MockBean
    private AvaliacaoRepository avRepo;

    @MockBean
    private FavoritoRepository favRepo;

    @MockBean
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void autenticarComo(Usuario usuario) {
        UsuarioDetails details = new UsuarioDetails(usuario);
        var auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Deve listar filmes com dados do admin criador")
    void listarFilmes() throws Exception {
        Usuario admin = new Usuario();
        admin.setNome("Admin");
        admin.setEmail("admin@example.com");
        admin.setTipoUsuario(TipoUsuario.ADMIN);

        Filme filme = new Filme();
        filme.setId(1L);
        filme.setTitulo("Matrix");
        filme.setAdminCriador(admin);

        when(filmeRepo.findAll()).thenReturn(List.of(filme));

        mockMvc.perform(get("/filmes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].titulo").value("Matrix"))
                .andExpect(jsonPath("$[0].adminNome").value("Admin"))
                .andExpect(jsonPath("$[0].adminEmail").value("admin@example.com"))
                .andExpect(jsonPath("$[0].tipoUsuario").value("ADMIN"));
    }


    // ----------------------------------------------------
    // AVALIAR FILME
    // ----------------------------------------------------
    @Test
    @DisplayName("Deve permitir que usuário autenticado avalie um filme")
    void avaliarFilmeComSucesso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@example.com");
        usuario.setTipoUsuario(TipoUsuario.COMUM);

        autenticarComo(usuario);

        Filme filme = new Filme();
        filme.setId(10L);
        filme.setTitulo("Inception");

        when(usuarioRepo.findByEmail("pedro@example.com")).thenReturn(Optional.of(usuario));
        when(filmeRepo.findById(10L)).thenReturn(Optional.of(filme));

        Avaliacao salvo = new Avaliacao();
        salvo.setId(100L);
        salvo.setNota(5);
        salvo.setComentario("Excelente");
        salvo.setDataAvaliacao(LocalDate.now());
        salvo.setUsuarioComum(usuario);
        salvo.setFilme(filme);

        when(avRepo.save(any(Avaliacao.class))).thenReturn(salvo);

        String json = """
                {
                  "nota": 5,
                  "comentario": "Excelente"
                }
                """;

        mockMvc.perform(
                        post("/filmes/10/avaliacoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.nota").value(5))
                .andExpect(jsonPath("$.comentario").value("Excelente"));

        // Captura avaliação enviada para o repositório para conferir dados
        ArgumentCaptor<Avaliacao> captor = ArgumentCaptor.forClass(Avaliacao.class);
        verify(avRepo).save(captor.capture());
        Avaliacao enviada = captor.getValue();

        assertThat(enviada.getUsuarioComum()).isEqualTo(usuario);
        assertThat(enviada.getFilme()).isEqualTo(filme);
        assertThat(enviada.getNota()).isEqualTo(5);
        assertThat(enviada.getComentario()).isEqualTo("Excelente");
    }

    // ----------------------------------------------------
    // LISTAR AVALIAÇÕES DE UM FILME
    // ----------------------------------------------------
    @Test
    @DisplayName("Deve listar avaliações de um filme existente")
    void listarAvaliacoesDeFilme() throws Exception {
        Filme filme = new Filme();
        filme.setId(10L);
        filme.setTitulo("Inception");

        when(filmeRepo.findById(10L)).thenReturn(Optional.of(filme));

        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setEmail("joao@example.com");
        usuario.setTipoUsuario(TipoUsuario.COMUM);

        Avaliacao av1 = new Avaliacao();
        av1.setId(1L);
        av1.setNota(4);
        av1.setComentario("Muito bom");
        av1.setDataAvaliacao(LocalDate.of(2025, 1, 1));
        av1.setUsuarioComum(usuario);
        av1.setFilme(filme);

        Avaliacao av2 = new Avaliacao();
        av2.setId(2L);
        av2.setNota(5);
        av2.setComentario("Obra-prima");
        av2.setDataAvaliacao(LocalDate.of(2025, 2, 2));
        av2.setUsuarioComum(usuario);
        av2.setFilme(filme);

        when(avRepo.findByFilmeId(10L)).thenReturn(List.of(av1, av2));

        mockMvc.perform(get("/filmes/10/avaliacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nota").value(4))
                .andExpect(jsonPath("$[0].usuarioComum.nome").value("João"))
                .andExpect(jsonPath("$[1].nota").value(5));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar listar avaliações de filme inexistente")
    void listarAvaliacoesFilmeInexistente() throws Exception {
        when(filmeRepo.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/filmes/999/avaliacoes"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Filme não encontrado"));
    }

    // ----------------------------------------------------
    // FAVORITAR FILME
    // ----------------------------------------------------
    @Test
    @DisplayName("Deve permitir favoritar filme para usuário autenticado")
    void favoritarFilme() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@example.com");
        usuario.setTipoUsuario(TipoUsuario.COMUM);

        autenticarComo(usuario);

        Filme filme = new Filme();
        filme.setId(5L);
        filme.setTitulo("Interestelar");

        when(usuarioRepo.findByEmail("pedro@example.com")).thenReturn(Optional.of(usuario));
        when(filmeRepo.findById(5L)).thenReturn(Optional.of(filme));

        Favorito favoritoSalvo = new Favorito();
        favoritoSalvo.setId(50L);
        favoritoSalvo.setUsuarioComum(usuario);
        favoritoSalvo.setFilme(filme);
        favoritoSalvo.setDataFavorito(LocalDate.now());

        when(favRepo.save(any(Favorito.class))).thenReturn(favoritoSalvo);

        mockMvc.perform(post("/filmes/5/favoritos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50L));

        ArgumentCaptor<Favorito> captor = ArgumentCaptor.forClass(Favorito.class);
        verify(favRepo).save(captor.capture());
        Favorito enviado = captor.getValue();

        assertThat(enviado.getUsuarioComum()).isEqualTo(usuario);
        assertThat(enviado.getFilme()).isEqualTo(filme);
    }

    // ----------------------------------------------------
    // CRIAR FILME (ADMIN)
    // ----------------------------------------------------
    @Test
    @DisplayName("Deve permitir que ADMIN crie filme")
    void criarFilmeComoAdmin() throws Exception {
        Usuario admin = new Usuario();
        admin.setNome("Admin");
        admin.setEmail("admin@example.com");
        admin.setTipoUsuario(TipoUsuario.ADMIN);

        autenticarComo(admin);

        Filme salvo = new Filme();
        salvo.setId(123L);
        salvo.setTitulo("Novo Filme");
        salvo.setAdminCriador(admin);

        when(filmeRepo.save(any(Filme.class))).thenReturn(salvo);

        FilmeRequest req = new FilmeRequest("Novo Filme");

        mockMvc.perform(
                        post("/filmes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(123L))
                .andExpect(jsonPath("$.titulo").value("Novo Filme"))
                .andExpect(jsonPath("$.adminNome").value("Admin"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar avaliar filme inexistente")
    void avaliarFilmeInexistente() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@example.com");
        usuario.setTipoUsuario(TipoUsuario.COMUM);

        autenticarComo(usuario);

        when(usuarioRepo.findByEmail("pedro@example.com")).thenReturn(Optional.of(usuario));
        when(filmeRepo.findById(999L)).thenReturn(Optional.empty());

        String json = """
            {
              "nota": 4,
              "comentario": "Tentando avaliar filme inexistente"
            }
            """;

        mockMvc.perform(
                        post("/filmes/999/avaliacoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Filme não encontrado"));

        verify(avRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar favoritar filme inexistente")
    void favoritarFilmeInexistente() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@example.com");
        usuario.setTipoUsuario(TipoUsuario.COMUM);

        autenticarComo(usuario);

        when(usuarioRepo.findByEmail("pedro@example.com")).thenReturn(Optional.of(usuario));
        when(filmeRepo.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/filmes/999/favoritos"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Filme não encontrado"));

        verify(favRepo, never()).save(any());
    }

    @Test
    @DisplayName("Ao criar filme, deve usar o ADMIN autenticado como adminCriador")
    void criarFilmeUsaAdminAutenticadoComoCriador() throws Exception {
        Usuario admin = new Usuario();
        admin.setNome("Admin");
        admin.setEmail("admin@example.com");
        admin.setTipoUsuario(TipoUsuario.ADMIN);

        autenticarComo(admin);

        Filme salvo = new Filme();
        salvo.setId(456L);
        salvo.setTitulo("Filme do Admin");
        salvo.setAdminCriador(admin);

        when(filmeRepo.save(any(Filme.class))).thenReturn(salvo);

        FilmeRequest req = new FilmeRequest("Filme do Admin");

        mockMvc.perform(
                        post("/filmes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isCreated());

        ArgumentCaptor<Filme> captor = ArgumentCaptor.forClass(Filme.class);
        verify(filmeRepo).save(captor.capture());
        Filme enviado = captor.getValue();

        assertThat(enviado.getTitulo()).isEqualTo("Filme do Admin");
        assertThat(enviado.getAdminCriador()).isEqualTo(admin);
    }


}
