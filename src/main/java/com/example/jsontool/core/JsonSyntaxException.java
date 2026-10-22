package com.example.jsontool.core;

/** Error saat parsing JSON, lengkap dengan posisi. */
public class JsonSyntaxException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int position;
    private final int line;
    private final int column;

    public JsonSyntaxException(String message, int position, int line, int column) {
        super(message);
        this.position = position;
        this.line = line;
        this.column = column;
    }

    public int getPosition() { return position; }
    public int getLine() { return line; }
    public int getColumn() { return column; }

    public String fullMessage() {
        return getMessage() + " (baris " + line + ", kolom " + column + ")";
    }
}
