package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpRequest {
    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final Map<String, String> parameters;
    private final String body;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders headers,
                        final Map<String, String> parameters, final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.parameters = parameters;
        this.body = body;
    }

    public static Optional<HttpRequest> from(final BufferedReader reader) throws IOException {
        final String startLine = reader.readLine();
        if (startLine == null) {
            return Optional.empty();
        }
        final RequestLine requestLine = RequestLine.from(startLine);
        final RequestHeaders headers = RequestHeaders.from(readHeaderLines(reader));
        final String body = readBody(reader, headers.getContentLength());

        return Optional.of(new HttpRequest(
                requestLine,
                headers,
                parseParameters(requestLine.getQueryString(), headers.getHeader("Content-Type"), body),
                body));
    }

    private static List<String> readHeaderLines(final BufferedReader reader) throws IOException {
        final List<String> headerLines = new ArrayList<>();
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            headerLines.add(line);
            line = reader.readLine();
        }
        return headerLines;
    }

    private static String readBody(final BufferedReader reader, final int contentLength) throws IOException {
        final char[] buffer = new char[contentLength];
        int total = 0;
        while (total < buffer.length) {
            final int read = reader.read(buffer, total, buffer.length - total);
            if (read == -1) {
                break;
            }
            total += read;
        }
        return new String(buffer, 0, total);
    }

    private static Map<String, String> parseParameters(
            final String queryString, final String contentType, final String body) {

        final Map<String, String> parameters = new HashMap<>();
        if (isFormUrlEncoded(contentType)) {
            parameters.putAll(parseFormData(body));
        }
        parameters.putAll(parseFormData(queryString));
        return parameters;
    }

    private static Map<String, String> parseFormData(final String formData) {
        final Map<String, String> parameters = new HashMap<>();
        if (formData.isEmpty()) {
            return parameters;
        }
        for (final String pair : formData.split("&")) {
            putParameter(parameters, pair);
        }
        return parameters;
    }

    private static boolean isFormUrlEncoded(final String contentType) {
        return contentType != null && contentType.startsWith(FORM_URLENCODED);
    }

    private static void putParameter(final Map<String, String> parameters, final String pair) {
        final int idx = pair.indexOf("=");
        if (idx == -1) {
            parameters.put(decode(pair), "");
            return;
        }
        parameters.put(decode(pair.substring(0, idx)), decode(pair.substring(idx + 1)));
    }

    private static String decode(final String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new HttpRequestParseException("URL 인코딩 형식이 잘못되었습니다.");
        }
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(headers.getHeader("Cookie"));
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public String getHeader(final String name) {
        return headers.getHeader(name);
    }

    public String getBody() {
        return body;
    }
}
