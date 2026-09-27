package org.apache.coyote.http11.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpVersion;

public final class HttpRequestParser {

    private static final String CONTENT_LENGTH = "content-length";
    private static final String CONTENT_TYPE = "content-type";
    private static final String COOKIE = "cookie";
    private static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";

    private HttpRequestParser() {
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        String[] requestLine = splitRequestLine(readLine(inputStream));
        HttpVersion version = HttpVersion.parse(requestLine[2]);
        HttpMethod method = parseMethod(requestLine[0]);
        RequestTarget target = parseRequestTarget(requestLine[1]);
        RequestHeaders headers = parseHeaders(inputStream);
        HttpBody body = readBody(inputStream, parseContentLength(headers));
        QueryParameters bodyParameters = parseBodyParameters(headers, body);
        HttpCookie cookie = parseCookie(headers);

        return new HttpRequest(
                method,
                target,
                version,
                headers,
                body,
                bodyParameters,
                cookie
        );
    }

    private static String[] splitRequestLine(String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("요청 라인이 존재하지 않습니다.");
        }

        String[] tokens = requestLine.trim().split("\\s+");
        if (tokens.length != 3) {
            throw new IllegalArgumentException("잘못된 HTTP 요청 라인입니다: " + requestLine);
        }

        return tokens;
    }

    private static HttpMethod parseMethod(String methodName) {
        try {
            return HttpMethod.valueOf(methodName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 HTTP Method입니다: " + methodName);
        }
    }

    private static RequestTarget parseRequestTarget(String rawTarget) {
        int queryIndex = rawTarget.indexOf('?');

        if (queryIndex == -1) {
            return new RequestTarget(rawTarget, new QueryParameters(Map.of()));
        }

        String path = rawTarget.substring(0, queryIndex);
        QueryParameters queryParameters = parseParameters(rawTarget.substring(queryIndex + 1));
        return new RequestTarget(path, queryParameters);
    }

    private static RequestHeaders parseHeaders(InputStream inputStream) throws IOException {
        Map<String, List<String>> headers = new HashMap<>();

        String line;
        while ((line = readLine(inputStream)) != null && !line.isEmpty()) {
            int separator = line.indexOf(':');
            if (separator <= 0) {
                throw new IllegalArgumentException("잘못된 HTTP 헤더입니다: " + line);
            }

            String name = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();
            headers.computeIfAbsent(name, ignored -> new ArrayList<>()).add(value);
        }

        return new RequestHeaders(headers);
    }

    private static String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int current;
        while ((current = inputStream.read()) != -1) {
            if (current == '\n') {
                break;
            }
            buffer.write(current);
        }

        if (current == -1 && buffer.size() == 0) {
            return null;
        }

        byte[] bytes = buffer.toByteArray();
        int length = bytes.length;
        if (length > 0 && bytes[length - 1] == '\r') {
            length--;
        }
        return new String(bytes, 0, length, StandardCharsets.ISO_8859_1);
    }

    private static int parseContentLength(RequestHeaders headers) {
        String value = headers.first(CONTENT_LENGTH).orElse("0");
        try {
            int contentLength = Integer.parseInt(value);
            if (contentLength < 0) {
                throw new IllegalArgumentException("잘못된 Content-Length입니다: " + value);
            }
            return contentLength;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 Content-Length입니다: " + value, e);
        }
    }

    private static HttpBody readBody(InputStream inputStream, int contentLength) throws IOException {
        byte[] bytes = inputStream.readNBytes(contentLength);
        if (bytes.length != contentLength) {
            throw new IOException(
                    "요청 Body가 Content-Length보다 짧습니다. "
                            + "expected=" + contentLength
                            + ", actual=" + bytes.length
            );
        }
        return new HttpBody(bytes);
    }

    private static QueryParameters parseBodyParameters(RequestHeaders headers, HttpBody body) {
        String contentType = headers.first(CONTENT_TYPE).orElse("");
        if (body.isEmpty() || !contentType.toLowerCase(Locale.ROOT).startsWith(FORM_URL_ENCODED)) {
            return new QueryParameters(Map.of());
        }
        return parseParameters(body.asString(StandardCharsets.UTF_8));
    }

    private static QueryParameters parseParameters(String rawParameters) {
        Map<String, List<String>> parameters = new HashMap<>();
        if (rawParameters == null || rawParameters.isBlank()) {
            return new QueryParameters(parameters);
        }

        for (String parameter : rawParameters.split("&")) {
            String[] pair = parameter.split("=", 2);
            String key = decode(pair[0]);
            String value = pair.length == 2 ? decode(pair[1]) : "";
            parameters.computeIfAbsent(key, ignored -> new ArrayList<>()).add(value);
        }

        return new QueryParameters(parameters);
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static HttpCookie parseCookie(RequestHeaders headers) {
        String cookieHeader = String.join("; ", headers.all(COOKIE));
        if (cookieHeader.isBlank()) {
            return new HttpCookie(Map.of());
        }

        Map<String, String> cookies = new HashMap<>();
        for (String pair : cookieHeader.split(";")) {
            String[] tokens = pair.trim().split("=", 2);
            if (tokens.length != 2) {
                continue;
            }
            cookies.put(tokens[0].trim(), tokens[1].trim());
        }

        return new HttpCookie(cookies);
    }
}
