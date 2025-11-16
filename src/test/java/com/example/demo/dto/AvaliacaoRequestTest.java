package com.example.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AvaliacaoRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve aceitar nota válida entre 0 e 5")
    void notaValida() {
        AvaliacaoRequest req = new AvaliacaoRequest(BigDecimal.valueOf(4), "Muito bom");

        Set<ConstraintViolation<AvaliacaoRequest>> violacoes = validator.validate(req);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar nota nula")
    void notaNula() {
        AvaliacaoRequest req = new AvaliacaoRequest(null, "Comentário");

        Set<ConstraintViolation<AvaliacaoRequest>> violacoes = validator.validate(req);

        assertThat(violacoes).isNotEmpty();
        assertThat(violacoes.iterator().next().getMessage()).isEqualTo("Nota é obrigatória");
    }

    @Test
    @DisplayName("Deve rejeitar nota menor que 0")
    void notaMenorQueZero() {
        AvaliacaoRequest req = new AvaliacaoRequest(BigDecimal.valueOf(-1), "Nota negativa");

        Set<ConstraintViolation<AvaliacaoRequest>> violacoes = validator.validate(req);

        assertThat(violacoes).isNotEmpty();
        assertThat(violacoes.iterator().next().getMessage()).isEqualTo("Nota mínima é 0");
    }

    @Test
    @DisplayName("Deve rejeitar nota maior que 5")
    void notaMaiorQueCinco() {
        AvaliacaoRequest req = new AvaliacaoRequest(BigDecimal.valueOf(10), "Nota fora da faixa");

        Set<ConstraintViolation<AvaliacaoRequest>> violacoes = validator.validate(req);

        assertThat(violacoes).isNotEmpty();
        assertThat(violacoes.iterator().next().getMessage()).isEqualTo("Nota máxima é 5");
    }
}
