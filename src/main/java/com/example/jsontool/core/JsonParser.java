package com.example.jsontool.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser JSON buatan sendiri.
 * lenient = true: toleran terhadap komentar, single quote, key tanpa quote, dan trailing comma.
 * Hasil: Map (LinkedHashMap) / List / String / JsonNumber / Boolean / null.
 */
public final class JsonParser {

    private static final String MSG_TRAILING_COMMA =
            "Trailing comma tidak diperbolehkan di JSON standar (pakai endpoint /api/json/fix untuk memperbaikinya)";
    private static final String MSG_SINGLE_QUOTE =
            "Single quote tidak diperbolehkan di JSON standar, pakai double quote (atau pakai endpoint /api/json/fix)";

    private final String s;
    private final boolean lenient;
    private int i = 0;

    private JsonParser(String s, boolean lenient) {
        this.s = s;
        this.lenient = lenient;
    }

    public static Object parse(String text, boolean lenient) {
        JsonParser p = new JsonParser(text == null ? "" : text, lenient);
        p.skipWs();
        if (p.i >= p.s.length()) throw p.err("Input kosong");
        Object v = p.value();
        p.skipWs();
        if (p.i < p.s.length()) {
            throw p.err("Karakter tak terduga '" + p.s.charAt(p.i) + "' setelah JSON selesai");
        }
        return v;
    }

    private JsonSyntaxException err(String msg) {
        int line = 1, col = 1;
        int end = Math.min(i, s.length());
        for (int k = 0; k < end; k++) {
            if (s.charAt(k) == '\n') { line++; col = 1; } else col++;
        }
        return new JsonSyntaxException(msg, end, line, col);
    }

    private void skipWs() {
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\uFEFF') { i++; continue; }
            if (lenient && c == '/' && i + 1 < s.length()) {
                char n = s.charAt(i + 1);
                if (n == '/') {
                    while (i < s.length() && s.charAt(i) != '\n') i++;
                    continue;
                }
                if (n == '*') {
                    int end = s.indexOf("*/", i + 2);
                    if (end < 0) throw err("Komentar /* tidak ditutup");
                    i = end + 2;
                    continue;
                }
            }
            break;
        }
    }

    private Object value() {
        skipWs();
        if (i >= s.length()) throw err("Data berakhir tiba-tiba, value diharapkan");
        char c = s.charAt(i);
        if (c == '\'' && !lenient) throw err(MSG_SINGLE_QUOTE);
        if (c == '{') return object();
        if (c == '[') return array();
        if (c == '"' || (lenient && c == '\'')) return string(c);
        if (c == '-' || (c >= '0' && c <= '9')) return number();
        if (s.startsWith("true", i))  { i += 4; return Boolean.TRUE; }
        if (s.startsWith("false", i)) { i += 5; return Boolean.FALSE; }
        if (s.startsWith("null", i))  { i += 4; return null; }
        throw err("Karakter tak terduga '" + c + "'");
    }

    private Map<String, Object> object() {
        Map<String, Object> m = new LinkedHashMap<>();
        i++; // {
        skipWs();
        if (i < s.length() && s.charAt(i) == '}') { i++; return m; }
        while (true) {
            skipWs();
            if (lenient && i < s.length() && s.charAt(i) == '}') { i++; return m; } // trailing comma
            if (!lenient && i < s.length() && s.charAt(i) == '}') throw err(MSG_TRAILING_COMMA);
            if (i >= s.length()) throw err("Object tidak ditutup, '}' diharapkan");
            char c = s.charAt(i);
            if (c == '\'' && !lenient) throw err(MSG_SINGLE_QUOTE);
            String key;
            if (c == '"' || (lenient && c == '\'')) {
                key = string(c);
            } else if (lenient && (Character.isLetter(c) || c == '_' || c == '$')) {
                int st = i;
                while (i < s.length() && (Character.isLetterOrDigit(s.charAt(i))
                        || s.charAt(i) == '_' || s.charAt(i) == '$')) i++;
                key = s.substring(st, i);
            } else {
                throw err("Key harus berupa string dalam tanda kutip");
            }
            skipWs();
            if (i >= s.length() || s.charAt(i) != ':') {
                throw err("Tanda ':' diharapkan setelah key \"" + key + "\"");
            }
            i++;
            m.put(key, value());
            skipWs();
            if (i >= s.length()) throw err("Object tidak ditutup, '}' diharapkan");
            char d = s.charAt(i);
            if (d == ',') { i++; continue; }
            if (d == '}') { i++; return m; }
            throw err("Diharapkan ',' atau '}' tapi ketemu '" + d + "'");
        }
    }

    private List<Object> array() {
        List<Object> l = new ArrayList<>();
        i++; // [
        skipWs();
        if (i < s.length() && s.charAt(i) == ']') { i++; return l; }
        while (true) {
            skipWs();
            if (lenient && i < s.length() && s.charAt(i) == ']') { i++; return l; } // trailing comma
            if (!lenient && i < s.length() && s.charAt(i) == ']') throw err(MSG_TRAILING_COMMA);
            l.add(value());
            skipWs();
            if (i >= s.length()) throw err("Array tidak ditutup, ']' diharapkan");
            char d = s.charAt(i);
            if (d == ',') { i++; continue; }
            if (d == ']') { i++; return l; }
            throw err("Diharapkan ',' atau ']' tapi ketemu '" + d + "'");
        }
    }

    private String string(char quote) {
        i++; // opening quote
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (i >= s.length()) throw err("String tidak ditutup");
            char c = s.charAt(i++);
            if (c == quote) return sb.toString();
            if (c == '\\') {
                if (i >= s.length()) throw err("Escape tidak lengkap");
                char e = s.charAt(i++);
                switch (e) {
                    case '"': sb.append('"'); break;
                    case '\'': sb.append('\''); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u':
                        if (i + 4 > s.length()) throw err("Escape \\u tidak lengkap");
                        try {
                            sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                        } catch (NumberFormatException ex) {
                            throw err("Escape \\u tidak valid");
                        }
                        i += 4;
                        break;
                    default:
                        throw err("Escape tidak valid: \\" + e);
                }
            } else if (c < 0x20 && !lenient) {
                i--;
                throw err("Karakter kontrol (newline/tab) harus di-escape di dalam string");
            } else {
                sb.append(c);
            }
        }
    }

    private JsonNumber number() {
        int st = i;
        if (s.charAt(i) == '-') i++;
        if (i >= s.length() || !Character.isDigit(s.charAt(i))) throw err("Angka tidak valid");
        if (s.charAt(i) == '0' && i + 1 < s.length() && Character.isDigit(s.charAt(i + 1))) {
            throw err("Angka tidak boleh diawali 0");
        }
        while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
        if (i < s.length() && s.charAt(i) == '.') {
            i++;
            if (i >= s.length() || !Character.isDigit(s.charAt(i))) throw err("Angka desimal tidak valid");
            while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
        }
        if (i < s.length() && (s.charAt(i) == 'e' || s.charAt(i) == 'E')) {
            i++;
            if (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) i++;
            if (i >= s.length() || !Character.isDigit(s.charAt(i))) throw err("Eksponen tidak valid");
            while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
        }
        return new JsonNumber(s.substring(st, i));
    }
}
