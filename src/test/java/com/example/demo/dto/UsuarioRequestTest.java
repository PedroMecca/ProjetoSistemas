package com.example.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve aceitar usuário válido")
    void usuarioValido() {
        UsuarioRequest req = new UsuarioRequest(
                "Pedro",
                "pedro@example.com",
                "senha123"
        );

        Set<ConstraintViolation<UsuarioRequest>> violacoes = validator.validate(req);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar e-mail inválido")
    void emailInvalido() {
        UsuarioRequest req = new UsuarioRequest(
                "Pedro",
                "email-invalido",
                "senha123"
        );

        Set<ConstraintViolation<UsuarioRequest>> violacoes = validator.validate(req);

        assertThat(violacoes).isNotEmpty();
        assertThat(violacoes.iterator().next().getMessage()).isEqualTo("E-mail inválido");
    }

    @Test
    @DisplayName("Deve rejeitar senha muito curta")
    void senhaCurta() {
        UsuarioRequest req = new UsuarioRequest(
                "Pedro",
                "pedro@example.com",
                "123"
        );

        Set<ConstraintViolation<UsuarioRequest>> violacoes = validator.validate(req);

        assertThat(violacoes).isNotEmpty();
        assertThat(violacoes.iterator().next().getMessage())
                .isEqualTo("Senha deve ter entre 6 e 100 caracteres");
    }
}
