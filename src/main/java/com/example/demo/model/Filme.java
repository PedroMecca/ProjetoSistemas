package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "filme")
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    // NOVOS CAMPOS
    private String categoria;

    private Integer ano;

    @Column(name = "poster_url")
    private String posterUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_admin", nullable = false)
    private Usuario adminCriador;

    public Filme() {}

    public Filme(Long id, String titulo, Usuario adminCriador) {
        this.id = id;
        this.titulo = titulo;
        this.adminCriador = adminCriador;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public Usuario getAdminCriador() { return adminCriador; }
    public void setAdminCriador(Usuario adminCriador) { this.adminCriador = adminCriador; }
}
