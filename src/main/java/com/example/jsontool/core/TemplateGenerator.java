package com.example.jsontool.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Template JSON default + pengisi placeholder acak. */
public final class TemplateGenerator {

    private static final Random RND = new Random();
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{(\\w+)}}");

    private static final String[] NAMES = {"Budi Santoso", "Siti Aminah", "Andi Wijaya", "Dewi Lestari",
            "Rizky Pratama", "Putri Ayu", "Agus Setiawan", "Maya Sari", "Dimas Anggara", "Rina Kartika"};
    private static final String[] CITIES = {"Jakarta", "Bandung", "Surabaya", "Yogyakarta", "Medan", "Denpasar"};
    private static final String[] PRODUCTS = {"Kopi Arabika 250g", "Keyboard Mekanik", "Headphone Bluetooth",
            "Tas Ransel", "Botol Minum", "Lampu Meja LED"};

    private static final Map<String, String> TEMPLATES = new LinkedHashMap<>();

    static {
        TEMPLATES.put("user",
                "{\"id\":\"{{uuid}}\",\"name\":\"{{name}}\",\"email\":\"{{email}}\",\"age\":{{age}},"
              + "\"active\":true,\"roles\":[\"user\",\"editor\"],"
              + "\"address\":{\"street\":\"Jl. Merdeka No. {{int}}\",\"city\":\"{{city}}\",\"country\":\"ID\"},"
              + "\"created_at\":\"{{now}}\"}");
        TEMPLATES.put("product",
                "{\"sku\":\"SKU-{{int}}\",\"name\":\"{{product}}\",\"price\":{{price}},\"stock\":{{int}},"
              + "\"in_stock\":true,\"tags\":[\"promo\",\"baru\"],\"rating\":4.5,\"updated_at\":\"{{now}}\"}");
        TEMPLATES.put("api-response",
                "{\"status\":\"success\",\"code\":200,\"message\":\"OK\","
              + "\"data\":{\"id\":\"{{uuid}}\",\"name\":\"{{name}}\"},"
              + "\"meta\":{\"page\":1,\"per_page\":10,\"total\":{{int}}},\"timestamp\":\"{{now}}\"}");
        TEMPLATES.put("config",
                "{\"app\":{\"name\":\"MyApp\",\"version\":\"1.0.0\",\"debug\":false},"
              + "\"server\":{\"host\":\"0.0.0.0\",\"port\":8080},"
              + "\"database\":{\"url\":\"jdbc:mysql://localhost:3306/mydb\",\"user\":\"root\",\"password\":\"\"},"
              + "\"features\":{\"login\":true,\"register\":true,\"dark_mode\":false}}");
        TEMPLATES.put("user-list",
                "{\"users\":[{\"id\":\"{{uuid}}\",\"name\":\"{{name}}\",\"email\":\"{{email}}\",\"city\":\"{{city}}\"},"
              + "{\"id\":\"{{uuid}}\",\"name\":\"{{name}}\",\"email\":\"{{email}}\",\"city\":\"{{city}}\"},"
              + "{\"id\":\"{{uuid}}\",\"name\":\"{{name}}\",\"email\":\"{{email}}\",\"city\":\"{{city}}\"}],"
              + "\"total\":3}");
    }

    private TemplateGenerator() { }

    public static List<String> names() {
        return new ArrayList<>(TEMPLATES.keySet());
    }

    /** Mengembalikan teks JSON (belum di-format) dengan placeholder terisi data acak. */
    public static String generate(String name) {
        String key = name == null ? "" : name.trim().toLowerCase();
        String tpl = TEMPLATES.get(key);
        if (tpl == null) {
            throw new IllegalArgumentException("Template '" + name + "' tidak ditemukan. Pilihan: " + names());
        }
        return PLACEHOLDER.matcher(tpl).replaceAll(m -> Matcher.quoteReplacement(value(m.group(1))));
    }

    private static String value(String token) {
        switch (token) {
            case "uuid":    return UUID.randomUUID().toString();
            case "name":    return NAMES[RND.nextInt(NAMES.length)];
            case "email":   return "user" + (100 + RND.nextInt(900)) + "@example.com";
            case "age":     return String.valueOf(18 + RND.nextInt(45));
            case "int":     return String.valueOf(1 + RND.nextInt(999));
            case "city":    return CITIES[RND.nextInt(CITIES.length)];
            case "product": return PRODUCTS[RND.nextInt(PRODUCTS.length)];
            case "price":   return String.valueOf((10 + RND.nextInt(990)) * 1000);
            case "now":     return Instant.now().toString();
            default:        return token;
        }
    }
}
