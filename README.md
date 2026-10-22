# JSON Tool API

A lightweight REST API with Swagger UI for **formatting, minifying, validating, fixing, comparing, and generating JSON**.

Built with Spring Boot 3 and Java 17. No database required.

## Getting Started

### Prerequisites

- **JDK 17+**
- **Maven 3.6+**

### Run the Application

```bash
mvn spring-boot:run
```

Open the following URLs after the application starts:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Build the JAR

```bash
mvn clean package
java -jar target/json-tool-api-1.0.0.jar
```

To change the application port:

```bash
java -jar target/json-tool-api-1.0.0.jar --server.port=9090
```

## API Endpoints

| Method | Path | Description |
|---|---|---|
| POST | `/api/json/format` | Beautifies JSON with strict syntax validation |
| POST | `/api/json/minify` | Minifies JSON into a single line |
| POST | `/api/json/fix` | Fixes common JSON syntax issues, including comments, single quotes, unquoted keys, and trailing commas |
| POST | `/api/json/validate` | Validates JSON and reports the error line and column |
| POST | `/api/json/compare` | Compares two JSON documents while ignoring object key order |
| GET | `/api/json/templates` | Lists the available default templates |
| POST | `/api/json/generate` | Generates JSON from a template using randomized data |

### Formatting Options

- `indent`: Sets the indentation style. Supported values are `"2"` (default), `"4"`, and `"tab"`.
- `sortKeys`: Sorts object keys alphabetically (A–Z).

## cURL Examples

### Format JSON

```bash
curl -X POST http://localhost:8080/api/json/format \
  -H "Content-Type: application/json" \
  -d '{"json":"{\"name\":\"Budi\",\"age\":25}","indent":"2"}'
```

### Fix Invalid JSON

```bash
curl -X POST http://localhost:8080/api/json/fix \
  -H "Content-Type: application/json" \
  -d '{"json":"{name:'"'"'Budi'"'"', skills:['"'"'java'"'"',],}"}'
```

### Validate JSON

```bash
curl -X POST http://localhost:8080/api/json/validate \
  -H "Content-Type: application/json" \
  -d '{"json":"{\"a\":1,}"}'
```

### Compare Two JSON Documents

```bash
curl -X POST http://localhost:8080/api/json/compare \
  -H "Content-Type: application/json" \
  -d '{"left":"{\"a\":1,\"b\":2}","right":"{\"a\":9,\"c\":3}"}'
```

### Generate JSON

```bash
curl -X POST http://localhost:8080/api/json/generate \
  -H "Content-Type: application/json" \
  -d '{"template":"user","indent":"2"}'
```

## Example Compare Response

```json
{
  "identical": false,
  "totalDifferences": 3,
  "added": 1,
  "removed": 1,
  "changed": 1,
  "differences": [
    {
      "type": "CHANGED",
      "path": "$.a",
      "leftValue": "1",
      "rightValue": "9"
    },
    {
      "type": "REMOVED",
      "path": "$.b",
      "leftValue": "2"
    },
    {
      "type": "ADDED",
      "path": "$.c",
      "rightValue": "3"
    }
  ]
}
```

## Example Error Response (HTTP 400)

```json
{
  "error": "INVALID_JSON",
  "message": "Object keys must be quoted strings",
  "line": 1,
  "column": 8,
  "position": 7
}
```

## Project Structure

```text
src/main/java/com/example/jsontool
├── JsonToolApplication.java
├── config/       OpenApiConfig (Swagger metadata and CORS)
├── controller/   JsonController, HomeController (redirects / to Swagger UI)
├── service/      JsonService
├── dto/          Request and response objects
├── exception/    GlobalExceptionHandler
└── core/         Parser, writer, differ, and template generator (no dependencies)
```

## About This Project

JSON Tool API is a small side project built for fun and experimentation using AI-assisted development (vibe coding).

It is still a work in progress, so bugs and unexpected behavior may occur. Feedback and suggestions are welcome!

**Use at your own risk.**