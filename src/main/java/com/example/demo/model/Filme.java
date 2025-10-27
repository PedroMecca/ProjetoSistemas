package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "filme")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Filme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_filme")
    private Integer id;

    @Column(name = "titulo", length = 150, nullable = false)
    private String titulo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "genero", length = 50)
    private String genero;

    @Column(name = "ano_lancamento")
    private Integer anoLancamento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario_admin")
    private Usuario usuarioAdmin;
}
