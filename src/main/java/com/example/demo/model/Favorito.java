package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "favorito")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_favorito")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario_comum")
    private Usuario usuarioComum;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_filme")
    private Filme filme;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_estado")
    private Estado estado;

    @Column(name = "data_favorito")
    private LocalDate dataFavorito;
}
