package com.example.jsontool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request perbandingan dua JSON")
public record CompareRequest(
        @Schema(description = "JSON kiri (asli)", example = "{\"name\":\"Budi\",\"age\":25}")
        @NotBlank(message = "field 'left' tidak boleh kosong")
        String left,

        @Schema(description = "JSON kanan (pembanding)", example = "{\"name\":\"Budi\",\"age\":26,\"city\":\"Jakarta\"}")
        @NotBlank(message = "field 'right' tidak boleh kosong")
        String right,

        @Schema(description = "true = toleran terhadap JSON yang agak kotor", defaultValue = "true")
        Boolean lenient) {
}
