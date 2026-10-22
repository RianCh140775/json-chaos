package com.example.jsontool.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Hasil proses JSON")
public record FormatResponse(
        @JsonRawValue
        @Schema(description = "JSON hasil proses, tertanam sebagai JSON asli (bukan string ter-escape)",
                type = "object",
                example = "{\"name\":\"Budi\",\"age\":25}")
        String result) {
}
