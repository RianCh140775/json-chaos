package com.example.jsontool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request validasi JSON")
public record ValidateRequest(
        @Schema(description = "Teks JSON mentah", example = "{\"name\":\"Budi\",\"age\":25}")
        @NotBlank(message = "field 'json' tidak boleh kosong")
        String json,

        @Schema(description = "true = toleran (komentar, single quote, trailing comma, key tanpa quote)",
                defaultValue = "false")
        Boolean lenient) {
}
