package com.example.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AvaliacaoRequest(

        @NotNull(message = "Nota é obrigatória")
        @Min(value = 0, message = "Nota mínima é 0")
        @Max(value = 5, message = "Nota máxima é 5")
        BigDecimal nota
) {}
