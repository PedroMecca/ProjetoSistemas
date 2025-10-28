package com.example.demo.dto;
import jakarta.validation.constraints.NotBlank;

public record FilmeRequest(@NotBlank String titulo) {}