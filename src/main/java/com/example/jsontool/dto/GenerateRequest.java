package com.example.jsontool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request generate JSON dari template default")
public record GenerateRequest(
        @Schema(description = "Nama template (lihat GET /api/json/templates)",
                allowableValues = {"user", "product", "api-response", "config", "user-list"},
                example = "user")
        @NotBlank(message = "field 'template' tidak boleh kosong")
        String template,

        @Schema(description = "Ukuran indent", allowableValues = {"2", "4", "tab"}, defaultValue = "2")
        String indent,

        @Schema(description = "Urutkan key A-Z", defaultValue = "false")
        Boolean sortKeys) {
}
