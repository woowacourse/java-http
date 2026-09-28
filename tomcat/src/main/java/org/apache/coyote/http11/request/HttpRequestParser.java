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

public class HttpRequestParser {

    public static RequestLine readRequestLine(InputStream input) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        int value;
        while ((value = input.read()) != -1 && value != '\n') {
            if (value != '\r') {
                bytes.write(value);
            }
        }

        return new RequestLine(bytes.toString());
    }

    public static Map<String, String> readHeaders(BufferedInputStream input) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = readRequestLine(input)) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");

            String name = line.substring(0, colonIndex).trim().toLowerCase(Locale.ROOT);
            String value = line.substring(colonIndex + 1).trim();
            headers.put(name, value);
        }
        return headers;
    }

    public static String readRequestBody(Map<String, String> headers, BufferedInputStream input) throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        byte[] bodyBytes = input.readNBytes(contentLength);
        if (bodyBytes.length != contentLength) {
            throw new EOFException("요청 본문이 중간에 끝났습니다.");
        }

        String requestBody = new String(bodyBytes, StandardCharsets.UTF_8);
        return requestBody;
    }
}
