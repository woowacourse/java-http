package org.apache.coyote;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequest {

    private static final char HEADER_DELIMITER = ':';
    private static final int REQUEST_LINE_TOKENS = 3;
    private static final String EMPTY_BODY = "";

    private final String method;
    private final String uri;
    private final String protocol;
    private final HttpHeader headers;
    private final String body;

    public HttpRequest(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String requestLine = reader.readLine();
            validateRequestLine(requestLine);

            StringTokenizer tokenizer = new StringTokenizer(requestLine);
            this.method = tokenizer.nextToken();
            this.uri = tokenizer.nextToken();
            this.protocol = tokenizer.nextToken();
            this.headers = parseHeaders(reader);
            this.body = parseBody(reader, headers.getContentLength());

        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getBody() {
        return body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers.getAllHeaders();
    }

    private HttpHeader parseHeaders(BufferedReader reader) throws IOException {
        HttpHeader headers = new HttpHeader();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(HEADER_DELIMITER);
            if (colonIndex != -1) {
                String headerName = line.substring(0, colonIndex).trim();
                String headerValue = line.substring(colonIndex + 1).trim();
                headers.add(headerName, headerValue);
            }
        }

        return headers;
    }

    private void validateRequestLine(String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("요청 형식이 올바르지 않습니다.");
        }

        final StringTokenizer tokenizer = new StringTokenizer(requestLine);
        if (tokenizer.countTokens() != REQUEST_LINE_TOKENS) {
            throw new IllegalArgumentException("요청 형식이 올바르지 않습니다.");
        }
    }

    private String parseBody(BufferedReader reader, String contentLength) throws IOException {
        if (contentLength == null) {
            return EMPTY_BODY;
        }

        int length;
        try {
            length = Integer.parseInt(contentLength);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Content-Length 값이 올바르지 않습니다: " + contentLength);
        }

        if (length < 0) {
            throw new IllegalArgumentException("Content-Length는 음수일 수 없습니다: " + contentLength);
        }

        if (length == 0) {
            return EMPTY_BODY;
        }

        char[] buffer = new char[length];
        reader.read(buffer, 0, length);
        return new String(buffer);
    }
}
