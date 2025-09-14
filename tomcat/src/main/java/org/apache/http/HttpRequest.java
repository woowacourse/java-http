package org.apache.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String protocol;

    private final Map<String, String> requestHeaders;
    private final Map<String, String> requestBodies;

    private final HttpCookie httpCookie;

    public HttpRequest(BufferedReader reader) throws IOException {
        Map<String, String> requestLines = parseRequestLine(reader);

        this.method = requestLines.get("Method");
        this.path = requestLines.get("Path");
        this.protocol = requestLines.get("Protocol");
        this.requestHeaders = parseHeader(reader);
        this.requestBodies = readBody(reader, requestHeaders);
        this.httpCookie = parseCookie(requestHeaders);
    }

    public String getMineType() throws IOException {
        Path path = Paths.get(getStaticFilePath());
        return Files.probeContentType(path);
    }

    public String getBodyAttribute(String name) {
        return requestBodies.get(name);
    }

    public String getHeaderAttribute(String name) {
        return requestHeaders.get(name);
    }

    public String getStaticFilePath() {
        if (path.contains(".")) {
            return "static" + path;
        }
        return "static" + path + ".html";
    }

    public boolean pathEquals(String mapping) {
        return path.equals(mapping);
    }

    private HttpCookie parseCookie(Map<String, String> headers) {
        HttpCookie httpCookie = new HttpCookie();
        if (headers.get("Cookie") != null) {
            httpCookie.parseCookie(headers.get("Cookie"));
        }
        return httpCookie;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public boolean containsCookie() {
        return requestHeaders.containsKey("Cookie");
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    private Map<String, String> parseRequestLine(BufferedReader reader) throws IOException {

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("유효하지 않은 요청입니다.");
        }
        Map<String, String> lines = new HashMap<>();
        String[] parts = requestLine.split(" ");

        if (parts.length >= 2) {
            lines.put("Method", parts[0]);
            lines.put("Path", parts[1]);
            if (parts.length >= 3) {
                lines.put("Protocol", parts[2]);
            }
        }

        return lines;
    }

    private Map<String, String> parseHeader(BufferedReader reader) throws IOException {
        String headerLine;
        Map<String, String> headers = new HashMap<>();
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            int separatorIndex = headerLine.indexOf(":");
            if (separatorIndex != -1) {
                String key = headerLine.substring(0, separatorIndex).trim();
                String value = headerLine.substring(separatorIndex + 1).trim();
                headers.put(key, value);
            }
        }
        return headers;
    }

    private Map<String, String> readBody(BufferedReader reader, Map<String, String> requestHeaders)
            throws IOException {
        Map<String, String> bodies = new HashMap<>();
        if (requestHeaders.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
            if (contentLength > 0) {
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);
                parseUrlEncodedParameters(requestBody, bodies);
            }
        }
        return bodies;
    }

    private void parseUrlEncodedParameters(String requestBody, Map<String, String> bodies) {
        String[] requests = requestBody.split("&");
        for (String request : requests) {
            String[] requestParts = request.split("=");
            bodies.put(requestParts[0], requestParts[1]);
        }
    }
}
