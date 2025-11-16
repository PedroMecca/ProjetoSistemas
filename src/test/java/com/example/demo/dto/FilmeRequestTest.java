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

class FilmeRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve aceitar título válido")
    void tituloValido() {
        FilmeRequest req = new FilmeRequest("Matrix");

        Set<ConstraintViolation<FilmeRequest>> violacoes = validator.validate(req);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar título vazio")
    void tituloVazio() {
        FilmeRequest req = new FilmeRequest(" ");

        Set<ConstraintViolation<FilmeRequest>> violacoes = validator.validate(req);

        assertThat(violacoes).isNotEmpty();
    }
}
