package org.apache.coyote.http11.helper;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.util.HttpStatus;

public final class Responses {

    private static final String CONTENT_TYPE = "Content-Type:";
    private static final String CONTENT_LENGTH = "Content-Length:";
    private static final String CRLF = "\r\n";
    private static final String SP   = " ";

    private Responses() {}

    public static void text(OutputStream out, String version, int code, String reason, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        writeHead(out, version, code, reason, "text/plain;charset=utf-8", bytes.length);
        out.write(bytes);
        out.flush();
    }

    public static void html(OutputStream out, String version, int code, String reason, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        writeHead(out, version, code, reason, "text/html;charset=utf-8", bytes.length);
        out.write(bytes);
        out.flush();
    }

    public static void binary(OutputStream out, String version, int code, String reason, String contentType, byte[] bytes) throws IOException {
        writeHead(out, version, code, reason, contentType, bytes.length);
        out.write(bytes);
        out.flush();
    }

    private static void writeHead(OutputStream out, String version, int code, String reason, String contentType, long len) throws IOException {
        String head =
                version + SP + code + SP + reason + SP + CRLF +
                        CONTENT_TYPE + SP + contentType + SP + CRLF +
                        CONTENT_LENGTH + SP + len + SP + CRLF +
                        CRLF;
        out.write(head.getBytes(StandardCharsets.UTF_8));
    }

    public static void notFound(OutputStream out, String version) throws IOException {
        text(out, version, HttpStatus.NOT_FOUND.code, HttpStatus.NOT_FOUND.reason, HttpStatus.NOT_FOUND.reason);
    }

    public static void serverError(OutputStream out, String version) throws IOException {
        text(out, version, HttpStatus.INTERNAL_SERVER_ERROR.code, HttpStatus.INTERNAL_SERVER_ERROR.reason, HttpStatus.INTERNAL_SERVER_ERROR.reason);
    }
}
