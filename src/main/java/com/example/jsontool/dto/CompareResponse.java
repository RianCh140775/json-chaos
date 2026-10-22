package com.example.jsontool.dto;

import com.example.jsontool.core.DiffItem;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Hasil perbandingan dua JSON")
public record CompareResponse(
        @Schema(description = "true jika kedua JSON identik (urutan key diabaikan)") boolean identical,
        @Schema(description = "Total perbedaan") int totalDifferences,
        @Schema(description = "Jumlah yang ditambahkan di kanan") int added,
        @Schema(description = "Jumlah yang dihapus di kanan") int removed,
        @Schema(description = "Jumlah yang nilainya berubah") int changed,
        @Schema(description = "Daftar perbedaan") List<DiffItem> differences) {
}
