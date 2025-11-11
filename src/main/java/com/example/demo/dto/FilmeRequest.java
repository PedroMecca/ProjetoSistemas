package com.example.demo.dto;
import jakarta.validation.constraints.NotBlank;

public record FilmeResponse(Long id, String titulo, String adminNome, String adminEmail, String tipoUsuario) {}