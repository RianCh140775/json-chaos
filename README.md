# JSON Tool API

REST API + Swagger UI untuk **format, minify, validate, fix, compare, dan generate JSON**
(Spring Boot 3, Java 17, tanpa database).

## Cara jalanin

Syarat: **JDK 17+** dan **Maven 3.6+**

```bash
mvn spring-boot:run
```

Buka Swagger UI: <http://localhost:8080/swagger-ui.html>  
OpenAPI JSON: <http://localhost:8080/v3/api-docs>

Build jar:

```bash
mvn clean package
java -jar target/json-tool-api-1.0.0.jar
```

Ganti port: `java -jar target/json-tool-api-1.0.0.jar --server.port=9090`

## Endpoint

| Method | Path | Fungsi |
|--------|------|--------|
| POST | `/api/json/format` | Beautify JSON (strict) |
| POST | `/api/json/minify` | Jadikan 1 baris |
| POST | `/api/json/fix` | Rapihin JSON kotor (komentar, single quote, key tanpa quote, trailing comma) |
| POST | `/api/json/validate` | Cek valid / tidak + baris & kolom error |
| POST | `/api/json/compare` | Bandingkan 2 JSON (urutan key diabaikan) |
| GET  | `/api/json/templates` | Daftar template default |
| POST | `/api/json/generate` | Generate JSON dari template (data acak) |

`indent`: `"2"` (default), `"4"`, atau `"tab"`. `sortKeys`: urutkan key A-Z.

## Contoh curl

```bash
# format
curl -X POST localhost:8080/api/json/format -H "Content-Type: application/json" \
  -d '{"json":"{\"name\":\"Budi\",\"age\":25}","indent":"2"}'

# fix JSON kotor
curl -X POST localhost:8080/api/json/fix -H "Content-Type: application/json" \
  -d '{"json":"{name:'"'"'Budi'"'"', skills:['"'"'java'"'"',],}"}'

# validate
curl -X POST localhost:8080/api/json/validate -H "Content-Type: application/json" \
  -d '{"json":"{\"a\":1,}"}'

# compare
curl -X POST localhost:8080/api/json/compare -H "Content-Type: application/json" \
  -d '{"left":"{\"a\":1,\"b\":2}","right":"{\"a\":9,\"c\":3}"}'

# generate
curl -X POST localhost:8080/api/json/generate -H "Content-Type: application/json" \
  -d '{"template":"user","indent":"2"}'
```

## Contoh response compare

```json
{
  "identical": false,
  "totalDifferences": 3,
  "added": 1,
  "removed": 1,
  "changed": 1,
  "differences": [
    {"type": "CHANGED", "path": "$.a", "leftValue": "1", "rightValue": "9"},
    {"type": "REMOVED", "path": "$.b", "leftValue": "2"},
    {"type": "ADDED",   "path": "$.c", "rightValue": "3"}
  ]
}
```

## Format error (HTTP 400)

```json
{
  "error": "INVALID_JSON",
  "message": "Key harus berupa string dalam tanda kutip",
  "line": 1,
  "column": 8,
  "position": 7
}
```

## Struktur project

```
src/main/java/com/example/jsontool
├── JsonToolApplication.java
├── config/       OpenApiConfig (info Swagger + CORS)
├── controller/   JsonController, HomeController (redirect / -> swagger)
├── service/      JsonService
├── dto/          request & response
├── exception/    GlobalExceptionHandler
└── core/         parser, writer, differ, template generator (tanpa dependency)
```
