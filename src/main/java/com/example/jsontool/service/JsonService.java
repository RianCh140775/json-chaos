package com.example.jsontool.service;

import com.example.jsontool.core.DiffItem;
import com.example.jsontool.core.DiffType;
import com.example.jsontool.core.JsonDiffer;
import com.example.jsontool.core.JsonParser;
import com.example.jsontool.core.JsonSyntaxException;
import com.example.jsontool.core.JsonWriter;
import com.example.jsontool.core.TemplateGenerator;
import com.example.jsontool.dto.CompareResponse;
import com.example.jsontool.dto.ValidateResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class JsonService {

    public String format(String json, String indent, boolean sortKeys, boolean lenient) {
        Object v = JsonParser.parse(json, lenient);
        return JsonWriter.write(v, resolveIndent(indent), sortKeys);
    }

    public String minify(String json, boolean sortKeys, boolean lenient) {
        Object v = JsonParser.parse(json, lenient);
        return JsonWriter.write(v, null, sortKeys);
    }

    public ValidateResponse validate(String json, boolean lenient) {
        try {
            Object v = JsonParser.parse(json, lenient);
            return new ValidateResponse(true, "JSON valid", null, null, rootType(v));
        } catch (JsonSyntaxException e) {
            return new ValidateResponse(false, e.getMessage(), e.getLine(), e.getColumn(), null);
        }
    }

    public CompareResponse compare(String left, String right, boolean lenient) {
        Object a;
        Object b;
        try {
            a = JsonParser.parse(left, lenient);
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException("JSON 'left' tidak valid: " + e.getMessage(),
                    e.getPosition(), e.getLine(), e.getColumn());
        }
        try {
            b = JsonParser.parse(right, lenient);
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException("JSON 'right' tidak valid: " + e.getMessage(),
                    e.getPosition(), e.getLine(), e.getColumn());
        }

        List<DiffItem> diffs = JsonDiffer.diff(a, b);
        int added = 0, removed = 0, changed = 0;
        for (DiffItem d : diffs) {
            if (d.type() == DiffType.ADDED) added++;
            else if (d.type() == DiffType.REMOVED) removed++;
            else changed++;
        }
        return new CompareResponse(diffs.isEmpty(), diffs.size(), added, removed, changed, diffs);
    }

    public List<String> templates() {
        return TemplateGenerator.names();
    }

    public String generate(String template, String indent, boolean sortKeys) {
        String raw = TemplateGenerator.generate(template);
        return format(raw, indent, sortKeys, false);
    }

    private static String resolveIndent(String indent) {
        if (indent == null || indent.isBlank() || indent.equals("2")) return "  ";
        if (indent.equals("4")) return "    ";
        if (indent.equalsIgnoreCase("tab")) return "\t";
        throw new IllegalArgumentException("indent harus salah satu dari: 2, 4, tab");
    }

    private static String rootType(Object v) {
        if (v == null) return "null";
        if (v instanceof Map) return "object";
        if (v instanceof List) return "array";
        if (v instanceof String) return "string";
        if (v instanceof Boolean) return "boolean";
        return "number";
    }
}
