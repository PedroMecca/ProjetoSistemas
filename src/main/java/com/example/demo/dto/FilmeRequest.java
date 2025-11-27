package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FilmeRequest(

        @NotBlank(message = "Título é obrigatório")
        @Size(min = 1, max = 255, message = "Título deve ter entre 1 e 255 caracteres")
        String titulo,

        // opcionais
        String categoria,
        Integer ano,
        String posterUrl
) {}
