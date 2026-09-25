package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final HttpCookie httpCookie;
    private final String body;

    public static HttpRequest from(InputStream inputStream) throws IOException {
        String rawRequestLine = readLine(inputStream);
        if (rawRequestLine == null || rawRequestLine.isEmpty()) {
            throw new IOException("HTTP 요청의 시작 줄을 읽을 수 없습니다.");
        }

        RequestLine requestLine = new RequestLine(rawRequestLine);
        Map<String, String> headers = readHeaders(inputStream);
        int contentLength = contentLength(headers);
        byte[] bodyBytes = inputStream.readNBytes(contentLength);

        if (bodyBytes.length != contentLength) {
            throw new IOException("HTTP 요청 본문을 모두 읽기 전에 연결이 종료되었습니다.");
        }

        String body = new String(bodyBytes, StandardCharsets.UTF_8);
        return new HttpRequest(requestLine, headers, body);
    }

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.headers.putAll(headers);
        this.httpCookie = createHttpCookie(this.headers);
        this.body = body;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getBodyParameters() {
        return parseParameters(body);
    }

    public Map<String, String> getQueryParameters() {
        return parseParameters(requestLine.getRawQuery());
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public String getBody() {
        return body;
    }

    public int getContentLength() {
        return contentLength(headers);
    }

    public boolean hasJsessionId() {
        return httpCookie.hasJsessionId();
    }

    public String getJsessionId() {
        return httpCookie.getJsessionId();
    }

    private static Map<String, String> readHeaders(InputStream inputStream) throws IOException {
        Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        while (true) {
            String line = readLine(inputStream);
            if (line == null) {
                throw new IOException("HTTP 요청 헤더를 모두 읽기 전에 연결이 종료되었습니다.");
            }
            if (line.isEmpty()) {
                return headers;
            }

            String[] header = line.split(":", 2);
            if (header.length != 2) {
                throw new IOException("올바르지 않은 HTTP 요청 헤더입니다: " + line);
            }
            headers.put(header[0].trim(), header[1].trim());
        }
    }

    private static String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();

        int value;
        while ((value = inputStream.read()) != -1 && value != '\n') {
            if (value != '\r') {
                line.write(value);
            }
        }

        if (value == -1) {
            if (line.size() == 0) {
                return null;
            }
            throw new IOException("HTTP 요청 줄을 모두 읽기 전에 연결이 종료되었습니다.");
        }

        return line.toString(StandardCharsets.US_ASCII);
    }

    private static int contentLength(Map<String, String> headers) {
        String contentLength = headers.get("Content-Length");
        return contentLength == null ? 0 : Integer.parseInt(contentLength);
    }

    private HttpCookie createHttpCookie(Map<String, String> headers) {
        String cookieHeader = headers.get("Cookie");

        return cookieHeader == null ? new HttpCookie() : new HttpCookie(cookieHeader);
    }

    private Map<String, String> parseParameters(String parameters) {
        return Arrays.stream(parameters.split("&"))
                .map(parameter -> parameter.split("=", 2))
                .collect(Collectors.toMap(
                        parts -> decode(parts[0]),
                        parts -> parts.length > 1 ? decode(parts[1]) : ""
                ));
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
