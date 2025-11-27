package com.example.demo.dto;

import com.example.demo.model.Avaliacao;
import com.example.demo.model.Usuario;

public record AvaliacaoResponse(
        Long id,
        Integer nota,       // <- era int
        String comentario,
        String dataAvaliacao,
        UsuarioResumo usuarioComum
) {
    public static AvaliacaoResponse fromEntity(Avaliacao av) {
        Usuario u = av.getUsuarioComum();

        return new AvaliacaoResponse(
                av.getId(),
                av.getNota(),                 // pode ser null
                av.getComentario(),
                av.getDataAvaliacao().toString(),
                new UsuarioResumo(u.getNome(), u.getEmail())
        );
    }

    public record UsuarioResumo(String nome, String email) { }
}

