package com.example.demo.controller;

import com.example.demo.dto.UsuarioRequest;
import com.example.demo.model.TipoUsuario;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepo;

    @MockBean
    private PasswordEncoder passwordEncoder;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve registrar um novo usuário COMUM com senha criptografada")
    void registrarUsuarioComSucesso() throws Exception {
        UsuarioRequest req = new UsuarioRequest(
                "Pedro",
                "pedro@example.com",
                "senha123"
        );

        when(usuarioRepo.existsByEmail("pedro@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("senha-criptografada");

        Usuario salvo = new Usuario();
        salvo.setNome("Pedro");
        salvo.setEmail("pedro@example.com");
        salvo.setSenha("senha-criptografada");
        salvo.setTipoUsuario(TipoUsuario.COMUM);

        when(usuarioRepo.save(any(Usuario.class))).thenReturn(salvo);

        mockMvc.perform(
                        post("/usuarios/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Pedro"))
                .andExpect(jsonPath("$.email").value("pedro@example.com"))
                .andExpect(jsonPath("$.tipoUsuario").value("COMUM"));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepo).save(captor.capture());

        Usuario enviado = captor.getValue();
        assertThat(enviado.getSenha()).isEqualTo("senha-criptografada");
    }

    @Test
    @DisplayName("Não deve permitir registro com e-mail já cadastrado")
    void naoDeveRegistrarEmailDuplicado() throws Exception {
        UsuarioRequest req = new UsuarioRequest(
                "Pedro",
                "pedro@example.com",
                "senha123"
        );

        when(usuarioRepo.existsByEmail("pedro@example.com")).thenReturn(true);

        mockMvc.perform(
                        post("/usuarios/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("E-mail já cadastrado"));

        verify(usuarioRepo, never()).save(any());
    }
}
