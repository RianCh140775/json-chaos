package com.example.jsontool.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Hasil validasi JSON")
public record ValidateResponse(
        @Schema(description = "Apakah JSON valid") boolean valid,
        @Schema(description = "Pesan hasil validasi", example = "JSON valid") String message,
        @Schema(description = "Baris error (jika tidak valid)") Integer line,
        @Schema(description = "Kolom error (jika tidak valid)") Integer column,
        @Schema(description = "Tipe root JSON jika valid", example = "object") String rootType) {
}
