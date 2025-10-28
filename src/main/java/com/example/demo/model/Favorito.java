package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "favorito")
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataFavorito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuarioComum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filme_id")
    private Filme filme;

    public Favorito() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDataFavorito() { return dataFavorito; }
    public void setDataFavorito(LocalDate dataFavorito) { this.dataFavorito = dataFavorito; }

    public Usuario getUsuarioComum() { return usuarioComum; }
    public void setUsuarioComum(Usuario usuarioComum) { this.usuarioComum = usuarioComum; }

    public Filme getFilme() { return filme; }
    public void setFilme(Filme filme) { this.filme = filme; }
}
