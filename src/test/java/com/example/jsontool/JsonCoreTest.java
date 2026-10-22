package com.example.jsontool;

import com.example.jsontool.core.DiffItem;
import com.example.jsontool.core.DiffType;
import com.example.jsontool.core.JsonDiffer;
import com.example.jsontool.core.JsonParser;
import com.example.jsontool.core.JsonSyntaxException;
import com.example.jsontool.core.JsonWriter;
import com.example.jsontool.core.TemplateGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonCoreTest {

    @Test
    void formatAndMinify() {
        Object v = JsonParser.parse("{\"a\":1,\"b\":[1,2]}", false);
        assertEquals("{\"a\":1,\"b\":[1,2]}", JsonWriter.write(v, null, false));
        assertTrue(JsonWriter.write(v, "  ", false).contains("\n  \"a\": 1"));
    }

    @Test
    void strictRejectsTrailingCommaButLenientAccepts() {
        assertThrows(JsonSyntaxException.class, () -> JsonParser.parse("{\"a\":1,}", false));
        Object v = JsonParser.parse("{a:'x', /* c */ b:[1,],}", true);
        assertEquals("{\"a\":\"x\",\"b\":[1]}", JsonWriter.write(v, null, false));
    }

    @Test
    void diffIgnoresKeyOrder() {
        Object a = JsonParser.parse("{\"a\":1,\"b\":2}", false);
        Object b = JsonParser.parse("{\"b\":2,\"a\":1}", false);
        assertTrue(JsonDiffer.diff(a, b).isEmpty());
    }

    @Test
    void diffFindsChanges() {
        Object a = JsonParser.parse("{\"a\":1,\"b\":2}", false);
        Object b = JsonParser.parse("{\"a\":9,\"c\":3}", false);
        List<DiffItem> d = JsonDiffer.diff(a, b);
        assertEquals(3, d.size());
        assertEquals(DiffType.CHANGED, d.get(0).type());
    }

    @Test
    void templatesProduceValidJson() {
        for (String name : TemplateGenerator.names()) {
            JsonParser.parse(TemplateGenerator.generate(name), false);
        }
    }
}
