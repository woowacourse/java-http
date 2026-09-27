package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public record HttpRequest(
        RequestLine requestLine,
        Map<String, String> headers,
        String body
) {

    public static HttpRequest parse(InputStream input) throws IOException {
        String firstLine = readLine(input);
        if (firstLine == null) {
            throw new IllegalArgumentException("요청 첫 줄이 없습니다.");
        }
        RequestLine requestLine = RequestLine.parse(firstLine);

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = readLine(input)) != null && !line.isEmpty()) {
            int colon = line.indexOf(':');
            if (colon > 0) {
                String name = line.substring(0, colon).trim().toLowerCase(Locale.ROOT);
                String value = line.substring(colon + 1).trim();
                headers.put(name, value);
            }
        }

        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        byte[] bodyBytes = input.readNBytes(contentLength);
        if (bodyBytes.length < contentLength) {
            throw new IOException("요청 본문이 Content-Length보다 짧습니다.");
        }

        return new HttpRequest(requestLine, headers, new String(bodyBytes, StandardCharsets.UTF_8));
    }

    private static String readLine(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int next;
        while ((next = input.read()) != -1) {
            if (next == '\n') {
                break;
            }
            buffer.write(next);
        }
        if (next == -1 && buffer.size() == 0) {
            return null;
        }

        byte[] bytes = buffer.toByteArray();
        int length = bytes.length;
        if (length > 0 && bytes[length - 1] == '\r') {
            length--;
        }
        return new String(bytes, 0, length, StandardCharsets.UTF_8);
    }

    public String parameter(String name) {
        String source = getSource();

        for (String parameter : source.split("&")) {
            String[] parts = parameter.split("=", 2);
            if (parts.length != 2) {
                continue;
            }

            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            if (key.equals(name)) {
                return URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private String getSource() {
        if (requestLine.method().equals("GET")) {
            return requestLine.query();
        }
        return body;
    }
}
