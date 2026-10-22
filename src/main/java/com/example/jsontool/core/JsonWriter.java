package com.example.jsontool.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Serializer JSON. indent == null berarti minify. */
public final class JsonWriter {

    private JsonWriter() { }

    public static String write(Object v, String indent, boolean sortKeys) {
        StringBuilder sb = new StringBuilder();
        write(v, sb, indent, 0, sortKeys);
        return sb.toString();
    }

    private static void newline(StringBuilder sb, String ind, int level) {
        if (ind == null) return;
        sb.append('\n');
        for (int k = 0; k < level; k++) sb.append(ind);
    }

    @SuppressWarnings("unchecked")
    private static void write(Object v, StringBuilder sb, String ind, int level, boolean sort) {
        if (v == null) {
            sb.append("null");
        } else if (v instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) v;
            if (m.isEmpty()) { sb.append("{}"); return; }
            List<String> keys = new ArrayList<>(m.keySet());
            if (sort) Collections.sort(keys);
            sb.append('{');
            boolean first = true;
            for (String k : keys) {
                if (!first) sb.append(',');
                first = false;
                newline(sb, ind, level + 1);
                quote(k, sb);
                sb.append(ind == null ? ":" : ": ");
                write(m.get(k), sb, ind, level + 1, sort);
            }
            newline(sb, ind, level);
            sb.append('}');
        } else if (v instanceof List) {
            List<Object> l = (List<Object>) v;
            if (l.isEmpty()) { sb.append("[]"); return; }
            sb.append('[');
            boolean first = true;
            for (Object o : l) {
                if (!first) sb.append(',');
                first = false;
                newline(sb, ind, level + 1);
                write(o, sb, ind, level + 1, sort);
            }
            newline(sb, ind, level);
            sb.append(']');
        } else if (v instanceof String) {
            quote((String) v, sb);
        } else if (v instanceof JsonNumber) {
            sb.append(((JsonNumber) v).raw());
        } else {
            sb.append(v);
        }
    }

    private static void quote(String str, StringBuilder sb) {
        sb.append('"');
        for (int k = 0; k < str.length(); k++) {
            char c = str.charAt(k);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        sb.append('"');
    }
}
