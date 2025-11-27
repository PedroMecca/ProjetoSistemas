package com.example.demo.dto;

public record FilmeResponse(
        Long id,
        String titulo,
        String categoria,
        Integer ano,
        String posterUrl,
        String adminNome,
        String adminEmail,
        String tipoUsuario
) {}
