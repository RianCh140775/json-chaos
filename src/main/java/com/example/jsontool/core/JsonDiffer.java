package com.example.jsontool.core;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Membandingkan dua struktur JSON. Urutan key diabaikan, array dibandingkan per index. */
public final class JsonDiffer {

    private JsonDiffer() { }

    public static List<DiffItem> diff(Object left, Object right) {
        List<DiffItem> out = new ArrayList<>();
        walk("$", left, right, out);
        return out;
    }

    @SuppressWarnings("unchecked")
    private static void walk(String path, Object a, Object b, List<DiffItem> out) {
        if (a instanceof Map && b instanceof Map) {
            Map<String, Object> ma = (Map<String, Object>) a;
            Map<String, Object> mb = (Map<String, Object>) b;
            Set<String> keys = new LinkedHashSet<>(ma.keySet());
            keys.addAll(mb.keySet());
            for (String k : keys) {
                String p = path + keyPath(k);
                boolean inA = ma.containsKey(k);
                boolean inB = mb.containsKey(k);
                if (inA && !inB) out.add(new DiffItem(DiffType.REMOVED, p, str(ma.get(k)), null));
                else if (!inA && inB) out.add(new DiffItem(DiffType.ADDED, p, null, str(mb.get(k))));
                else walk(p, ma.get(k), mb.get(k), out);
            }
        } else if (a instanceof List && b instanceof List) {
            List<Object> la = (List<Object>) a;
            List<Object> lb = (List<Object>) b;
            int n = Math.max(la.size(), lb.size());
            for (int k = 0; k < n; k++) {
                String p = path + "[" + k + "]";
                if (k >= la.size()) out.add(new DiffItem(DiffType.ADDED, p, null, str(lb.get(k))));
                else if (k >= lb.size()) out.add(new DiffItem(DiffType.REMOVED, p, str(la.get(k)), null));
                else walk(p, la.get(k), lb.get(k), out);
            }
        } else if (!sameScalar(a, b)) {
            out.add(new DiffItem(DiffType.CHANGED, path, str(a), str(b)));
        }
    }

    private static String keyPath(String key) {
        if (key.matches("[A-Za-z_$][A-Za-z0-9_$]*")) return "." + key;
        return "['" + key.replace("'", "\\'") + "']";
    }

    private static String str(Object v) {
        return JsonWriter.write(v, null, false);
    }

    private static boolean sameScalar(Object a, Object b) {
        if (a == null || b == null) return a == b;
        if (a instanceof JsonNumber && b instanceof JsonNumber) {
            return ((JsonNumber) a).value().compareTo(((JsonNumber) b).value()) == 0;
        }
        if (a instanceof Map || a instanceof List || b instanceof Map || b instanceof List) return false;
        return a.equals(b);
    }
}
