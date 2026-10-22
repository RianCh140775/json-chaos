package com.example.jsontool.core;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Satu perbedaan antara JSON kiri dan kanan")
public record DiffItem(
        @Schema(description = "Jenis perbedaan") DiffType type,
        @Schema(description = "Lokasi perbedaan", example = "$.address.city") String path,
        @Schema(description = "Nilai di JSON kiri (null jika tidak ada)", example = "\"Jakarta\"") String leftValue,
        @Schema(description = "Nilai di JSON kanan (null jika tidak ada)", example = "\"Bandung\"") String rightValue) {
}
