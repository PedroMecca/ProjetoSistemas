package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.security.UsuarioDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/filmes")
public class FilmeController {

    private final FilmeRepository filmeRepo;
    private final AvaliacaoRepository avRepo;
    private final FavoritoRepository favRepo;
    private final UsuarioRepository usuarioRepo;
    private final ComentarioRepository comentarioRepo;

    public FilmeController(FilmeRepository filmeRepo,
                           AvaliacaoRepository avRepo,
                           FavoritoRepository favRepo,
                           UsuarioRepository usuarioRepo,
                           ComentarioRepository comentarioRepo) {
        this.filmeRepo = filmeRepo;
        this.avRepo = avRepo;
        this.favRepo = favRepo;
        this.usuarioRepo = usuarioRepo;
        this.comentarioRepo = comentarioRepo;
    }


    // ----------------------------------------------------
    // LISTAR FILMES (ADMIN + COMUM)
    // ----------------------------------------------------
    @GetMapping
    public ResponseEntity<List<FilmeResponse>> listar() {
        List<FilmeResponse> filmes = filmeRepo.findAll().stream()
                .map(f -> new FilmeResponse(
                        f.getId(),
                        f.getTitulo(),
                        f.getCategoria(),
                        f.getAno(),
                        f.getPosterUrl(),
                        f.getAdminCriador().getNome(),
                        f.getAdminCriador().getEmail(),
                        f.getAdminCriador().getTipoUsuario().name()
                ))
                .toList();

        return ResponseEntity.ok(filmes);
    }


    // ----------------------------------------------------
// CRIAR / ATUALIZAR AVALIAÇÃO (nota) – ADMIN + COMUM
// ----------------------------------------------------
    @PostMapping("/{id}/avaliacoes")
    public ResponseEntity<Avaliacao> avaliar(@PathVariable Long id,
                                             @RequestBody @Valid AvaliacaoRequest req) {

        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UsuarioDetails) principal).getUsername();

        Usuario usuario = usuarioRepo.findByEmail(email).orElseThrow();

        Filme filme = filmeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Filme não encontrado"
                ));

        // Busca se já existe avaliação desse usuário para esse filme
        var existenteOpt = avRepo.findByUsuarioComumIdAndFilmeId(usuario.getId(), id);

        Avaliacao avaliacao = existenteOpt.orElseGet(Avaliacao::new);

        avaliacao.setFilme(filme);
        avaliacao.setUsuarioComum(usuario);
        avaliacao.setNota(req.nota().intValue());   // sempre sobrescreve a nota
        avaliacao.setDataAvaliacao(LocalDate.now());
        // se sua entidade Avaliacao ainda tiver campo comentario, você pode deixar null ou manter o que já tem

        Avaliacao salvo = avRepo.save(avaliacao);
        return ResponseEntity.ok(salvo);
    }
    // ----------------------------------------------------
    // LISTAR AVALIAÇÕES DE UM FILME (para painel ADMIN)
    // ----------------------------------------------------
    @GetMapping("/{id}/avaliacoes")
    public ResponseEntity<List<AvaliacaoResponse>> listarAvaliacoes(@PathVariable Long id) {

        Filme filme = filmeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Filme não encontrado"
                ));

        List<AvaliacaoResponse> avaliacoes = avRepo.findByFilmeId(filme.getId())
                .stream()
                .map(AvaliacaoResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(avaliacoes);
    }

    // ----------------------------------------------------
    // FAVORITAR FILME (ADMIN + COMUM)
    // ----------------------------------------------------
    @PostMapping("/{id}/favoritos")
    public ResponseEntity<Favorito> favoritar(@PathVariable Long id) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UsuarioDetails) principal).getUsername();

        Usuario usuario = usuarioRepo.findByEmail(email).orElseThrow();

        Filme filme = filmeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Filme não encontrado"
                ));

        Favorito f = new Favorito();
        f.setId(null);
        f.setUsuarioComum(usuario);
        f.setFilme(filme);
        f.setDataFavorito(LocalDate.now());

        Favorito salvo = favRepo.save(f);
        return ResponseEntity.ok(salvo);
    }


    // ----------------------------------------------------
    // CRIAR FILME (SOMENTE ADMIN)
    // ----------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FilmeResponse> criarFilme(@RequestBody @Valid FilmeRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UsuarioDetails usuarioDetails = (UsuarioDetails) auth.getPrincipal();
        Usuario admin = usuarioDetails.getUsuario();

        Filme f = new Filme();
        f.setTitulo(req.titulo());
        f.setCategoria(req.categoria());
        f.setAno(req.ano());
        f.setPosterUrl(req.posterUrl());
        f.setAdminCriador(admin);

        Filme salvo = filmeRepo.save(f);

        FilmeResponse response = new FilmeResponse(
                salvo.getId(),
                salvo.getTitulo(),
                salvo.getCategoria(),
                salvo.getAno(),
                salvo.getPosterUrl(),
                admin.getNome(),
                admin.getEmail(),
                admin.getTipoUsuario().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ----------------------------------------------------
// ADICIONAR COMENTÁRIO (ADMIN + COMUM)
// ----------------------------------------------------
    @PostMapping("/{id}/comentarios")
    public ResponseEntity<ComentarioResponse> comentar(@PathVariable Long id,
                                                       @RequestBody @Valid ComentarioRequest req) {

        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UsuarioDetails) principal).getUsername();

        Usuario usuario = usuarioRepo.findByEmail(email).orElseThrow();

        Filme filme = filmeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Filme não encontrado"
                ));

        Comentario c = new Comentario();
        c.setTexto(req.texto());
        c.setDataComentario(LocalDate.now());
        c.setUsuarioComum(usuario);
        c.setFilme(filme);

        Comentario salvo = comentarioRepo.save(c);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ComentarioResponse.fromEntity(salvo));
    }

    // ----------------------------------------------------
// LISTAR COMENTÁRIOS DE UM FILME
// ----------------------------------------------------
    @GetMapping("/{id}/comentarios")
    public ResponseEntity<List<ComentarioResponse>> listarComentarios(@PathVariable Long id) {

        Filme filme = filmeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Filme não encontrado"
                ));

        List<ComentarioResponse> comentarios = comentarioRepo
                .findByFilmeIdOrderByDataComentarioDesc(filme.getId())
                .stream()
                .map(ComentarioResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(comentarios);
    }


}
