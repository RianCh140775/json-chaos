package com.example.jsontool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request berisi teks JSON yang akan diproses")
public record JsonRequest(
        @Schema(description = "Teks JSON mentah (dalam bentuk string)",
                example = "{\"name\":\"Budi\",\"age\":25,\"skills\":[\"java\",\"sql\"]}")
        @NotBlank(message = "field 'json' tidak boleh kosong")
        String json,

        @Schema(description = "Ukuran indent", allowableValues = {"2", "4", "tab"}, defaultValue = "2")
        String indent,

        @Schema(description = "Urutkan key A-Z", defaultValue = "false")
        Boolean sortKeys) {
}
