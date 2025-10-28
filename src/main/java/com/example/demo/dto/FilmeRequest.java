package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record FilmeRequest(
        @NotBlank String titulo,
        @NotBlank String descricao,
        @NotBlank String diretor,
        @Positive int duracao,
        @NotBlank String genero,
        @Min(1888) int anoLancamento
) { }
