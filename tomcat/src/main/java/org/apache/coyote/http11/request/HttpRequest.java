package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.exception.ContentLengthExceededException;
import org.apache.coyote.http11.StartLine;

public class HttpRequest {

    private final StartLine startLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(
            final StartLine startLine,
            final Map<String, String> headers,
            final String body
    ) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest form(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final StartLine startLine = StartLine.from(bufferedReader.readLine());
        if (startLine.isNull()) {
            return null;
        }
        final Map<String, String> headers = parseHeaders(bufferedReader);
        final String body = parseBody(bufferedReader, headers);
        return new HttpRequest(startLine, headers, body);
    }

    private static Map<String, String> parseHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String header;
        while ((header = bufferedReader.readLine()) != null) {
            if (header.isBlank()) {
                break;
            }
            final String[] parsedHeader = header.split(": ");
            headers.put(parsedHeader[0], parsedHeader[1]);
        }
        return headers;
    }

    private static String parseBody(final BufferedReader bufferedReader, final Map<String, String> headers)
            throws IOException {
        if (headers.containsKey("Content-Length")) {
            int expectLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[expectLength];
            int actualLength = bufferedReader.read(buffer, 0, expectLength);
            if (actualLength != expectLength) {
                throw new ContentLengthExceededException("Content-Length 와 Request Body 길이가 같지 않습니다.");
            }
            return new String(buffer);
        }
        return null;
    }

    public String findHeaderValue(String headerTitle) {
        return headers.getOrDefault(headerTitle, null);
    }

    public StartLine getStartLine() {
        return startLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
