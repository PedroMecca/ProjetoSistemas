package com.example.demo.controller;

import com.example.demo.dto.AvaliacaoRequest;
import com.example.demo.model.Avaliacao;
import com.example.demo.model.Favorito;
import com.example.demo.model.Filme;
import com.example.demo.model.Usuario;
import com.example.demo.repository.AvaliacaoRepository;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.FilmeRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.UsuarioDetails;
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

    public FilmeController(FilmeRepository filmeRepo, AvaliacaoRepository avRepo,
                           FavoritoRepository favRepo, UsuarioRepository usuarioRepo) {
        this.filmeRepo = filmeRepo;
        this.avRepo = avRepo;
        this.favRepo = favRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @GetMapping
    public List<Filme> listar() {
        return filmeRepo.findAll();
    }

    @PostMapping("/{id}/avaliacoes")
    public ResponseEntity<?> avaliar(@PathVariable Integer id, @RequestBody AvaliacaoRequest req) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UsuarioDetails) principal).getUsername();
        Usuario usuario = usuarioRepo.findByEmail(email).orElseThrow();
        Filme filme = filmeRepo.findById(id).orElseThrow();

        Avaliacao a = new Avaliacao();
        a.setId(null);
        // converte BigDecimal -> int (use intValueExact() se quiser garantir sem perda)
        a.setNota(req.nota() != null ? req.nota().intValue() : null);
        a.setComentario(req.comentario());
        a.setDataAvaliacao(LocalDate.now());
        a.setUsuarioComum(usuario);
        a.setFilme(filme);

        avRepo.save(a);
        return ResponseEntity.ok(a);
    }

    @PostMapping("/{id}/favoritos")
    public ResponseEntity<?> favoritar(@PathVariable Integer id) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UsuarioDetails) principal).getUsername();
        Usuario usuario = usuarioRepo.findByEmail(email).orElseThrow();
        Filme filme = filmeRepo.findById(id).orElseThrow();

        Favorito f = new Favorito();
        f.setId(null);
        f.setUsuarioComum(usuario);
        f.setFilme(filme);
        f.setDataFavorito(LocalDate.now());

        favRepo.save(f);
        return ResponseEntity.ok(f);
    }
}
