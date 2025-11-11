package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record FilmeRequest(
        @NotBlank(message = "O título do filme é obrigatório")
        String titulo
) {}
