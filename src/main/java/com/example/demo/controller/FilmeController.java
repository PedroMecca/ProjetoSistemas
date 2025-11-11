package com.example.demo.controller;

import com.example.demo.dto.AvaliacaoRequest;
import com.example.demo.dto.FilmeRequest;
import com.example.demo.dto.FilmeResponse;
import com.example.demo.model.Avaliacao;
import com.example.demo.model.Favorito;
import com.example.demo.model.Filme;
import com.example.demo.model.Usuario;
import com.example.demo.repository.AvaliacaoRepository;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.FilmeRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.UsuarioDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/filmes")
public class FilmeController {

    private final FilmeRepository filmeRepo;
    private final AvaliacaoRepository avRepo;
    private final FavoritoRepository favRepo;
    private final UsuarioRepository usuarioRepo;

    public FilmeController(FilmeRepository filmeRepo,
                           AvaliacaoRepository avRepo,
                           FavoritoRepository favRepo,
                           UsuarioRepository usuarioRepo) {
        this.filmeRepo = filmeRepo;
        this.avRepo = avRepo;
        this.favRepo = favRepo;
        this.usuarioRepo = usuarioRepo;
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
                        f.getAdminCriador().getNome(),
                        f.getAdminCriador().getEmail(),
                        f.getAdminCriador().getTipoUsuario().name()
                ))
                .toList();

        return ResponseEntity.ok(filmes);
    }

    @PostMapping("/{id}/avaliacoes")
    public ResponseEntity<Avaliacao> avaliar(@PathVariable Long id,
                                             @RequestBody @Valid AvaliacaoRequest req) {

        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UsuarioDetails) principal).getUsername();

        Usuario usuario = usuarioRepo.findByEmail(email).orElseThrow();
        Filme filme = filmeRepo.findById(id).orElseThrow();

        Avaliacao a = new Avaliacao();
        a.setId(null);
        a.setNota(req.nota() != null ? req.nota().intValue() : null);
        a.setComentario(req.comentario());
        a.setDataAvaliacao(LocalDate.now());
        a.setUsuarioComum(usuario);
        a.setFilme(filme);

        Avaliacao salvo = avRepo.save(a);
        return ResponseEntity.ok(salvo);
    }

    // ----------------------------------------------------
    // FAVORITAR FILME (ADMIN + COMUM)
    // ----------------------------------------------------
    @PostMapping("/{id}/favoritos")
    public ResponseEntity<Favorito> favoritar(@PathVariable Long id) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UsuarioDetails) principal).getUsername();

        Usuario usuario = usuarioRepo.findByEmail(email).orElseThrow();
        Filme filme = filmeRepo.findById(id).orElseThrow();

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
        f.setAdminCriador(admin);

        Filme salvo = filmeRepo.save(f);

        FilmeResponse response = new FilmeResponse(
                salvo.getId(),
                salvo.getTitulo(),
                admin.getNome(),
                admin.getEmail(),
                admin.getTipoUsuario().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
