package com.example.demo.dto;

public record FilmeResponse(
        Long id,
        String titulo,
        String adminNome,
        String adminEmail,
        String tipoUsuario
) {}
