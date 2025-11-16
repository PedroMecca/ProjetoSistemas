package com.example.demo.dto;

import com.example.demo.model.Avaliacao;
import com.example.demo.model.Usuario;

public record AvaliacaoResponse(
        Long id,
        int nota,
        String comentario,
        String dataAvaliacao,
        UsuarioResumo usuarioComum
) {
    public static AvaliacaoResponse fromEntity(Avaliacao av) {
        Usuario u = av.getUsuarioComum(); // 👈 bate com setUsuarioComum

        return new AvaliacaoResponse(
                av.getId(),
                av.getNota(),
                av.getComentario(),
                av.getDataAvaliacao().toString(), // pode formatar depois
                new UsuarioResumo(
                        u.getNome(),
                        u.getEmail()
                )
        );
    }

    public record UsuarioResumo(String nome, String email) { }
}
