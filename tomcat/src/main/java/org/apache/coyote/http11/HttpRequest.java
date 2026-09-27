package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
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

    public static HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        String firstLine = bufferedReader.readLine();
        if (firstLine == null) {
            throw new IllegalArgumentException("요청 첫 줄이 없습니다.");
        }
        RequestLine requestLine = RequestLine.parse(firstLine);

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            int colon = line.indexOf(':');
            if (colon > 0) {
                String name = line.substring(0, colon).trim().toLowerCase(Locale.ROOT);
                String value = line.substring(colon + 1).trim();
                headers.put(name, value);
            }
        }

        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        char[] buffer = new char[contentLength];
        int readCount = 0;
        while (readCount < contentLength) {
            int count = bufferedReader.read(buffer, readCount, contentLength - readCount);
            if (count == -1) {
                throw new IOException("요청 본문이 Content-Length보다 짧습니다.");
            }
            readCount += count;
        }

        return new HttpRequest(requestLine, headers, new String(buffer));
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
