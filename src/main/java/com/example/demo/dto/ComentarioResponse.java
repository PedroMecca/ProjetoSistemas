// src/main/java/com/example/demo/dto/ComentarioResponse.java
package com.example.demo.dto;

import com.example.demo.model.Comentario;
import com.example.demo.model.Usuario;

public record ComentarioResponse(
        Long id,
        String texto,
        String dataComentario,
        UsuarioResumo usuarioComum
) {
    public static ComentarioResponse fromEntity(Comentario c) {
        Usuario u = c.getUsuarioComum();
        return new ComentarioResponse(
                c.getId(),
                c.getTexto(),
                c.getDataComentario().toString(),
                new UsuarioResumo(u.getNome(), u.getEmail())
        );
    }

    public record UsuarioResumo(String nome, String email) { }
}
