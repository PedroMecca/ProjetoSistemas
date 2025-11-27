// src/main/java/com/example/demo/dto/ComentarioRequest.java
package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record ComentarioRequest(
        @NotBlank(message = "Comentário não pode ser vazio")
        String texto
) {}
