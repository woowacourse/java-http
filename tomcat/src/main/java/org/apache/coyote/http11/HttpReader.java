package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpReader {

    private static final String NONE_HEADER = "";

    private final String startLine;
    private final HttpHeaders httpHeaders;
    private final String body;

    public HttpReader(final BufferedReader bufferedReader) throws IOException {
        this.startLine = extractStartLine(bufferedReader);
        this.httpHeaders = new HttpHeaders(extractHeaders(bufferedReader));
        this.body = extractBody(bufferedReader);
    }

    private String extractStartLine(final BufferedReader bufferedReader) throws IOException {
        if (!bufferedReader.ready()) {
            throw new IllegalArgumentException("요청 값이 없습니다.");
        }
        return bufferedReader.readLine();
    }

    private List<String> extractHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();
            if (NONE_HEADER.equals(line)) {
                break;
            }
            lines.add(line);
        }
        return lines;
    }

    private String extractBody(final BufferedReader bufferedReader) throws IOException {
        if (!bufferedReader.ready()) {
            return "";
        }
        int contentLength = Integer.parseInt(this.httpHeaders.getValue(CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public String getStartLine() {
        return this.startLine;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public String getBody() {
        return this.body;
    }
}
