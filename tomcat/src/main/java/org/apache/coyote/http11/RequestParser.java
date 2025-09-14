package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    private final BufferedReader bufferedReader;

    public RequestParser(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public static HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        return new RequestParser(bufferedReader).parseRequest();
    }

    public HttpRequest parseRequest() throws IOException {
        String startLine = readRequestLine();
        String[] requestLine = parseRequestLine(startLine);
        String method = requestLine[0];
        String requestTarget = requestLine[1];
        String version = requestLine[2];

        Map<String, String> headers = parseHeaders();

        String body = parseBody(headers);

        String path = extractPath(requestTarget);
        Map<String, String> queryParams = parseQueryString(requestTarget);

        return new HttpRequest(
                method,
                path,
                version,
                headers,
                body,
                queryParams
        );
    }

    private String readRequestLine() throws IOException {
        String startLine = this.bufferedReader.readLine();
        if (startLine == null || startLine.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid request: empty start line");
        }
        return startLine;
    }

    private String[] parseRequestLine(String startLine) {
        String[] parts = startLine.split(" ", 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid HTTP request format: " + startLine);
        }

        return parts;
    }

    private Map<String, String> parseHeaders() throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = this.bufferedReader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }
        return headers;
    }

    private String parseBody(Map<String, String> headers) throws IOException {
        String contentLengthHeader = headers.get("Content-Length");
        if (contentLengthHeader == null || contentLengthHeader.isEmpty()) {
            return "";
        }

        try {
            int contentLength = Integer.parseInt(contentLengthHeader);
            if (contentLength <= 0) {
                return "";
            }

            char[] buffer = new char[contentLength];
            int totalRead = 0;

            while (totalRead < contentLength) {
                int read = this.bufferedReader.read(buffer, totalRead, contentLength - totalRead);
                if (read == -1) {
                    break;
                }
                totalRead += read;
            }

            return new String(buffer, 0, totalRead);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Content-Length header: " + contentLengthHeader);
        }
    }

    private String extractPath(String requestTarget) {
        int queryStart = requestTarget.indexOf("?");
        if (queryStart == -1) {
            return requestTarget;
        }
        return requestTarget.substring(0, queryStart);
    }

    private Map<String, String> parseQueryString(String requestTarget) {
        Map<String, String> params = new HashMap<>();

        int queryStart = requestTarget.indexOf("?");
        if (queryStart == -1) {
            return params;
        }

        String queryString = requestTarget.substring(queryStart + 1);
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }

        return params;
    }
}
