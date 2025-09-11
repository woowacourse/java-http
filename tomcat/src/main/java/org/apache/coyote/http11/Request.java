package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Request {

    private static final String SP = " ";
    private static final int METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int SKIP_REQUEST_LINE = 1;
    private static final int FIELD_NAME_INDEX = 0;
    private static final int FIELD_VALUE_NAME = 1;
    private static final String CONTENT_LENGTH = "content-length";
    private static final String COOKIE = "cookie";

    private final String method;
    private final String requestURI;
    private final Map<String, String> headers;
    private final String messageBody;

    public Request(final InputStream inputStream) throws IOException {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final List<String> lines = readRequestAndHeaderLines(reader);
            final List<String> requestLine = parseRequestLine(lines);
            this.method = requestLine.get(METHOD_INDEX);
            this.requestURI = requestLine.get(REQUEST_URI_INDEX);
            this.headers = parseHeaders(lines);
            this.messageBody = parseMessageBody(reader);
        } catch (
                final IllegalArgumentException |
                      ArrayIndexOutOfBoundsException |
                      NullPointerException |
                      IOException |
                      NoSuchElementException e
        ) {
            throw new IOException("HTTP Request를 해석할 수 없습니다.", e);
        }
    }

    public Cookies getCookies() {
        if (!containsHeader(COOKIE)) {
            return null;
        }
        return new Cookies(getHeader(COOKIE));
    }

    private List<String> readRequestAndHeaderLines(final BufferedReader reader) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }
        return lines;
    }

    private List<String> parseRequestLine(final List<String> lines) {
        final String firstLine = lines.getFirst();
        return Arrays.stream(firstLine.split(SP, 3))
                .toList();
    }

    private Map<String, String> parseHeaders(final List<String> lines) {
        return lines.stream()
                .skip(SKIP_REQUEST_LINE)
                .map(headerLine -> headerLine.split(":", 2))
                .collect(Collectors.toMap(
                        splitByColon -> splitByColon[FIELD_NAME_INDEX].trim().toLowerCase(),
                        splitByColon -> splitByColon[FIELD_VALUE_NAME].trim()
                ));
    }

    private String parseMessageBody(final BufferedReader requestReader) throws IOException {
        if (!headers.containsKey(CONTENT_LENGTH)) {
            return null;
        }
        final int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
        final char[] buffer = new char[contentLength];
        requestReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean containsHeader(final String filedName) {
        return headers.containsKey(filedName.toLowerCase());
    }

    public String getHeader(final String filedName) {
        return headers.get(filedName.toLowerCase());
    }

    public String getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
