package org.apache.coyote.http11.request;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.coyote.http11.request.bodyParser.BodyParserResolver;

public final class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parse(
            BufferedInputStream input
    ) throws IOException {
        String requestLineValue = readRequestLine(input);

        if (requestLineValue == null || requestLineValue.isBlank()) {
            throw new EOFException("요청 라인을 읽을 수 없습니다.");
        }

        RequestLine requestLine = new RequestLine(requestLineValue);
        Map<String, String> headers = readHeaders(input);
        String rawBody = readRequestBody(headers, input);

        String contentType =
                headers.getOrDefault("content-type", "");

        Map<String, String> parsedBody = BodyParserResolver.parse(contentType, rawBody);

        return new HttpRequest(
                requestLine,
                headers,
                parsedBody
        );
    }

    public static String readRequestLine(
            InputStream input
    ) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        int value;
        while ((value = input.read()) != -1) {
            if (value == '\n') {
                break;
            }

            if (value != '\r') {
                bytes.write(value);
            }
        }

        if (value == -1 && bytes.size() == 0) {
            return null;
        }

        return bytes.toString(StandardCharsets.UTF_8);
    }

    public static Map<String, String> readHeaders(
            BufferedInputStream input
    ) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;
        while ((line = readRequestLine(input)) != null
                && !line.isEmpty()) {
            int colonIndex = line.indexOf(':');

            if (colonIndex <= 0) {
                throw new IllegalArgumentException(
                        "올바르지 않은 HTTP 헤더입니다: " + line
                );
            }

            String name = line.substring(0, colonIndex)
                    .trim()
                    .toLowerCase(Locale.ROOT);

            String value = line.substring(colonIndex + 1)
                    .trim();

            headers.put(name, value);
        }

        return headers;
    }

    public static String readRequestBody(
            Map<String, String> headers,
            BufferedInputStream input
    ) throws IOException {
        int contentLength = parseContentLength(headers);

        if (contentLength == 0) {
            return "";
        }

        byte[] bodyBytes = input.readNBytes(contentLength);

        if (bodyBytes.length != contentLength) {
            throw new EOFException(
                    "요청 본문이 중간에 끝났습니다."
            );
        }

        return new String(
                bodyBytes,
                StandardCharsets.UTF_8
        );
    }

    private static int parseContentLength(
            Map<String, String> headers
    ) {
        String contentLengthValue =
                headers.getOrDefault("content-length", "0");

        try {
            int contentLength =
                    Integer.parseInt(contentLengthValue);

            if (contentLength < 0) {
                throw new IllegalArgumentException(
                        "Content-Length는 0 이상이어야 합니다."
                );
            }

            return contentLength;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Content-Length가 올바르지 않습니다.",
                    exception
            );
        }
    }
}
