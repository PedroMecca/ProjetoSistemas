package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

@Entity
@Table(name = "avaliacao")
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(0)
    @Max(5)
    private Integer nota;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    private LocalDate dataAvaliacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuarioComum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filme_id")
    private Filme filme;

    public Avaliacao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDate getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDate dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }

    public Usuario getUsuarioComum() { return usuarioComum; }
    public void setUsuarioComum(Usuario usuarioComum) { this.usuarioComum = usuarioComum; }

    public Filme getFilme() { return filme; }
    public void setFilme(Filme filme) { this.filme = filme; }
}
