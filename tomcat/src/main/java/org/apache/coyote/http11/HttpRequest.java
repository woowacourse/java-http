package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {
    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final String body;

    private HttpRequest(final RequestLine requestLine, final Map<String, String> headers,
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
        final Map<String, String> headers = parseHeaders(reader);
        final String body = readBody(reader, headers);

        return Optional.of(new HttpRequest(
                requestLine,
                headers,
                parseParameters(requestLine.getQueryString(), headers, body),
                body));
    }

    private static Map<String, String> parseHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            putHeader(headers, line);
            line = reader.readLine();
        }
        return headers;
    }

    private static String readBody(final BufferedReader reader, final Map<String, String> headers) throws IOException {
        final String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return "";
        }
        final char[] buffer = new char[toContentLength(contentLength)];
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

    private static int toContentLength(final String contentLength) {
        try {
            final int length = Integer.parseInt(contentLength);
            if (length < 0) {
                throw new HttpRequestParseException("Content-Length가 음수입니다: " + contentLength);
            }
            return length;
        } catch (NumberFormatException e) {
            throw new HttpRequestParseException("Content-Length 형식이 잘못되었습니다: " + contentLength);
        }
    }

    private static Map<String, String> parseParameters(
            final String queryString, final Map<String, String> headers, final String body) {

        final Map<String, String> parameters = new HashMap<>();
        if (isFormUrlEncoded(headers)) {
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

    private static boolean isFormUrlEncoded(final Map<String, String> headers) {
        final String contentType = headers.get("Content-Type");
        return contentType != null && contentType.startsWith(FORM_URLENCODED);
    }

    private static void putHeader(final Map<String, String> headers, final String line) {
        final int idx = line.indexOf(":");
        if (idx == -1) {
            throw new HttpRequestParseException("헤더 형식이 잘못되었습니다: " + line);
        }
        headers.put(line.substring(0, idx).trim(), line.substring(idx + 1).trim());
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
        return HttpCookie.from(headers.get("Cookie"));
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }
}
