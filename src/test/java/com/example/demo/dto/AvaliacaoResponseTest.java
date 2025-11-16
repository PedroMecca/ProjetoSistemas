package com.example.demo.dto;

import com.example.demo.model.Avaliacao;
import com.example.demo.model.Filme;
import com.example.demo.model.TipoUsuario;
import com.example.demo.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AvaliacaoResponseTest {

    @Test
    @DisplayName("Deve mapear corretamente Avaliacao para AvaliacaoResponse")
    void fromEntityDeveMapearCamposCorretamente() {
        Usuario usuario = new Usuario();
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@example.com");
        usuario.setTipoUsuario(TipoUsuario.COMUM);

        Filme filme = new Filme();
        filme.setId(1L);
        filme.setTitulo("Matrix");

        Avaliacao av = new Avaliacao();
        av.setId(10L);
        av.setNota(5);
        av.setComentario("Muito bom");
        av.setDataAvaliacao(LocalDate.of(2025, 1, 1));
        av.setUsuarioComum(usuario);
        av.setFilme(filme);

        AvaliacaoResponse resp = AvaliacaoResponse.fromEntity(av);

        assertThat(resp.id()).isEqualTo(10L);
        assertThat(resp.nota()).isEqualTo(5);
        assertThat(resp.comentario()).isEqualTo("Muito bom");
        assertThat(resp.dataAvaliacao()).isEqualTo("2025-01-01");
        assertThat(resp.usuarioComum().nome()).isEqualTo("Pedro");
        assertThat(resp.usuarioComum().email()).isEqualTo("pedro@example.com");
    }
}
