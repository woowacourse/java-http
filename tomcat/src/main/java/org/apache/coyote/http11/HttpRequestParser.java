package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parse(final BufferedReader reader) throws IOException {
        final String requestLine = reader.readLine();
        if (requestLine == null) {
            return null;
        }

        final String[] requestLineParts = requestLine.trim().split("\\s+");
        if (requestLineParts.length != 3) {
            throw new IllegalArgumentException("잘못된 HTTP 요청 라인입니다: " + requestLine);
        }

        final Map<String, String> headers = new LinkedHashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            final String[] header = line.split(":", 2);
            if (header.length != 2) {
                throw new IllegalArgumentException("잘못된 HTTP 헤더입니다: " + line);
            }
            headers.put(header[0].trim().toLowerCase(), header[1].trim());
        }

        final int contentLength = parseContentLength(headers);
        final char[] body = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            final int readCount = reader.read(body, totalRead, contentLength - totalRead);
            if (readCount < 0) {
                throw new IOException("요청 본문이 Content-Length보다 짧습니다.");
            }
            totalRead += readCount;
        }

        return new HttpRequest(
                requestLineParts[0],
                requestLineParts[1],
                requestLineParts[2],
                headers,
                new String(body)
        );
    }

    private static int parseContentLength(final Map<String, String> headers) {
        final String contentLength = headers.get("content-length");
        return contentLength == null ? 0 : Integer.parseInt(contentLength);
    }
}
