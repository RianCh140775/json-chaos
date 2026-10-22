package com.example.jsontool.controller;

import com.example.jsontool.dto.CompareRequest;
import com.example.jsontool.dto.CompareResponse;
import com.example.jsontool.dto.ErrorResponse;
import com.example.jsontool.dto.FormatResponse;
import com.example.jsontool.dto.GenerateRequest;
import com.example.jsontool.dto.JsonRequest;
import com.example.jsontool.dto.ValidateRequest;
import com.example.jsontool.dto.ValidateResponse;
import com.example.jsontool.service.JsonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/json", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "JSON Tool", description = "Format, minify, validate, fix, compare, dan generate JSON")
public class JsonController {

    private final JsonService service;

    public JsonController(JsonService service) {
        this.service = service;
    }

    @Operation(summary = "Format / beautify JSON",
            description = "Merapikan JSON valid (strict) dengan indent 2 spasi, 4 spasi, atau tab.")
    @ApiResponse(responseCode = "200", description = "Berhasil")
    @ApiResponse(responseCode = "400", description = "JSON tidak valid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "/format", consumes = MediaType.APPLICATION_JSON_VALUE)
    public FormatResponse format(@Valid @RequestBody JsonRequest req) {
        return new FormatResponse(service.format(req.json(), req.indent(), flag(req.sortKeys(), false), false));
    }

    @Operation(summary = "Minify JSON",
            description = "Menghapus semua whitespace sehingga JSON jadi satu baris.")
    @ApiResponse(responseCode = "400", description = "JSON tidak valid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "/minify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public FormatResponse minify(@Valid @RequestBody JsonRequest req) {
        return new FormatResponse(service.minify(req.json(), flag(req.sortKeys(), false), false));
    }

    @Operation(summary = "Fix & rapihin JSON kotor",
            description = "Toleran terhadap komentar (// dan /* */), single quote, key tanpa quote, dan trailing comma. "
                    + "Hasilnya JSON standar yang rapi.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = JsonRequest.class),
            examples = @ExampleObject(name = "json-kotor", value = """
                    {"json":"{name:'Budi', age:25, skills:['java','sql',],}","indent":"2","sortKeys":false}""")))
    @ApiResponse(responseCode = "400", description = "JSON tidak bisa diperbaiki",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "/fix", consumes = MediaType.APPLICATION_JSON_VALUE)
    public FormatResponse fix(@Valid @RequestBody JsonRequest req) {
        return new FormatResponse(service.format(req.json(), req.indent(), flag(req.sortKeys(), false), true));
    }

    @Operation(summary = "Validate JSON",
            description = "Selalu mengembalikan 200. Cek field 'valid'. Jika tidak valid, ada pesan beserta baris dan kolom error.")
    @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ValidateResponse validate(@Valid @RequestBody ValidateRequest req) {
        return service.validate(req.json(), flag(req.lenient(), false));
    }

    @Operation(summary = "Bandingkan dua JSON",
            description = "Urutan key diabaikan, array dibandingkan per index. Hasil: daftar path yang ditambah, dihapus, atau berubah.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CompareRequest.class),
            examples = @ExampleObject(name = "contoh-compare", value = """
                    {"left":"{'name':'Budi','age':25,'tags':['a','b']}","right":"{'name':'Budi','age':26,'tags':['a'],'city':'Jakarta'}","lenient":true}""")))
    @ApiResponse(responseCode = "400", description = "Salah satu JSON tidak valid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "/compare", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CompareResponse compare(@Valid @RequestBody CompareRequest req) {
        return service.compare(req.left(), req.right(), flag(req.lenient(), true));
    }

    @Operation(summary = "Daftar template default")
    @GetMapping("/templates")
    public List<String> templates() {
        return service.templates();
    }

    @Operation(summary = "Generate JSON otomatis dari template default",
            description = "Data diisi acak (uuid, nama, email, kota, harga, timestamp). Hasil sudah dirapihin.")
    @ApiResponse(responseCode = "400", description = "Template tidak ditemukan",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public FormatResponse generate(@Valid @RequestBody GenerateRequest req) {
        return new FormatResponse(service.generate(req.template(), req.indent(), flag(req.sortKeys(), false)));
    }

    private static boolean flag(Boolean value, boolean defaultValue) {
        return value == null ? defaultValue : value;
    }
}
