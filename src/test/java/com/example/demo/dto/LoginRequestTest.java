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

class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve aceitar login válido")
    void loginValido() {
        LoginRequest req = new LoginRequest("pedro@example.com", "senha123");

        Set<ConstraintViolation<LoginRequest>> violacoes = validator.validate(req);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar login sem e-mail")
    void emailVazio() {
        LoginRequest req = new LoginRequest(" ", "senha123");

        Set<ConstraintViolation<LoginRequest>> violacoes = validator.validate(req);

        assertThat(violacoes).isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar login sem senha")
    void senhaVazia() {
        LoginRequest req = new LoginRequest("pedro@example.com", " ");

        Set<ConstraintViolation<LoginRequest>> violacoes = validator.validate(req);

        assertThat(violacoes).isNotEmpty();
    }
}
