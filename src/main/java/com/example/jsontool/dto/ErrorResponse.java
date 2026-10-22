package com.example.jsontool.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Format error standar")
public record ErrorResponse(
        @Schema(example = "INVALID_JSON") String error,
        @Schema(example = "Key harus berupa string dalam tanda kutip") String message,
        @Schema(description = "Baris error JSON") Integer line,
        @Schema(description = "Kolom error JSON") Integer column,
        @Schema(description = "Posisi karakter error JSON") Integer position) {
}
