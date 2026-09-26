package com.techcourse.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, List<String>> headers;
    private final byte[] body;

    private final SessionManager sessionManager;
    private Session session;
    private boolean newSession;

    public static HttpRequest parse(InputStream inputStream, SessionManager sessionManager) throws IOException {

        RequestLine requestLine = readFirstLine(inputStream);
        Map<String, List<String>> headers = readHeaders(inputStream);
        byte[] body = readBody(inputStream, headers);

        return new HttpRequest(requestLine, headers, body, sessionManager);
    }

    public HttpSession getSession(boolean create) {
        if (session != null) {
            return session;
        }

        boolean validSessionExists = sessionManager.hasValidSession(headers);

        session = sessionManager.getSession(headers, create);

        if (session != null && create && !validSessionExists) {
            newSession = true;
        }

        return session;
    }

    public boolean hasNewSession() {
        return newSession;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
    private static byte[] readBody(InputStream inputStream, Map<String, List<String>> headers) throws IOException {

        List<String> contentLengthValues = headers.getOrDefault("content-length", List.of("0"));

        if (contentLengthValues.isEmpty()) {
            return new byte[0];
        }

        String contentLengthValue = contentLengthValues.getFirst().trim();

        // 숫자로만 이루어지지 않은 경우
        if (!contentLengthValue.matches("\\d+")) {
            throw new IOException(
                    "Invalid Content-Length: " + contentLengthValue
            );
        }

        // content-length가 다중으로 들어왔는데 값이 다를 경우
        for (String value : contentLengthValues) {
            if (!contentLengthValue.equals(value.trim())) {
                throw new IOException("Invalid Content-Length: " + contentLengthValue);
            }
        }

        int contentLength = Integer.parseInt(contentLengthValue);

        if (contentLength < 0) {
            throw new IOException("Invalid Content-Length: " + contentLengthValue);
        }

        byte[] body = new byte[contentLength];
        int offset = 0;

        while (offset < contentLength) {
            int read = inputStream.read(body, offset, contentLength - offset);

            if (read == -1) {
                throw new IOException("End of stream reached");
            }

            offset += read;
        }

        return body;
    }
    private static Map<String, List<String>> readHeaders(InputStream inputStream) throws IOException {

        Map<String, List<String>> headers = new HashMap<>();
        String headerLine;

        while ((headerLine = readLine(inputStream)) != null && !headerLine.isEmpty()) {
            int colonIndex = headerLine.indexOf(':');

            if (colonIndex <= 0) {
                throw new IllegalArgumentException("Invalid header line: " + headerLine);
            }

            String headerName = headerLine.substring(0, colonIndex).trim().toLowerCase(Locale.ROOT);
            String headerValue = headerLine.substring(colonIndex + 1).trim();

            headers.computeIfAbsent(headerName, key -> new ArrayList<>()).add(headerValue);
        }

        return headers;
    }

    private static RequestLine readFirstLine(InputStream inputStream) throws IOException {

        String firstLine = readLine(inputStream);

        if (firstLine == null || firstLine.isEmpty()) {
            throw new IllegalArgumentException("Invalid request line: " + firstLine);
        }

        return RequestLine.parse(firstLine);
    }

    private static String readLine(InputStream inputStream) throws IOException {
        StringBuilder line = new StringBuilder();

        int current;
        boolean carriageReturn = false;

        while ((current = inputStream.read()) != -1) {
            if (current == '\r') {
                carriageReturn = true;
                continue;
            }

            if (carriageReturn && current == '\n') {
                break;
            }

            if (carriageReturn) {
                line.append('\r');
                carriageReturn = false;
            }

            line.append((char) current);
        }

        if (current == -1 && line.isEmpty()) {
            return null;
        }

        return line.toString();
    }

    private HttpRequest(RequestLine requestLine, Map<String, List<String>> headers, byte[] body, SessionManager sessionManager) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.sessionManager = sessionManager;
    }
}
