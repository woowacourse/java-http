package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Http11Parser {

    private static final String REQUEST_HEADER_SUFFIX = "";

    public static Http11Request readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line = bufferedReader.readLine();
        if (line.isEmpty()) {
            throw new IllegalArgumentException("HttpRequest가 비어있습니다.");
        }
        while (!REQUEST_HEADER_SUFFIX.equals(line)) {
            lines.add(line);
            line = bufferedReader.readLine();
        }
        return new Http11Request(lines);
    }

    public static String writeHttpResponse(final Http11Response response) {
        StringBuilder serializedResponse = new StringBuilder();
        serializedResponse.append("HTTP/1.1 ").append(response.getStatusCode()).append(" \r\n");
        if (response.getLocation() != null) {
            serializedResponse.append("Location: ").append(response.getLocation()).append(" \r\n");
            serializedResponse.append("Content-Length: 0 \r\n");
            serializedResponse.append("\r\n");
        }
        if (response.getContent() != null) {
            serializedResponse.append("Content-Type: ").append(response.getContentType()).append(";charset=utf-8 \r\n");
            serializedResponse.append("Content-Length: ").append(response.getContentLength()).append(" \r\n");
            serializedResponse.append("\r\n");
            serializedResponse.append(response.getBody());
        }
        return serializedResponse.toString();
    }
}
