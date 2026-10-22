package com.example.jsontool.core;

import java.math.BigDecimal;

/** Angka disimpan sebagai teks asli supaya presisi/format tidak berubah. */
public final class JsonNumber {
    private final String raw;

    public JsonNumber(String raw) { this.raw = raw; }

    public String raw() { return raw; }

    public BigDecimal value() { return new BigDecimal(raw); }
}
