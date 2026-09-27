package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String HEADER_SEPARATOR = ":";
    private static final String PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String CONTENT_LENGTH = "content-length";
    private static final String COOKIE = "cookie";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> bodyParams;

    private HttpRequest(final RequestLine requestLine,
                        final Map<String, String> headers,
                        final Map<String, String> bodyParams) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.bodyParams = bodyParams;
    }

    /**
     * @return 파싱된 요청, 또는 클라이언트가 데이터 없이 연결을 종료했다면 null
     */
    public static HttpRequest from(final InputStream rawInputStream) throws IOException {
        final InputStream inputStream = new BufferedInputStream(rawInputStream);

        final String requestLine = readLine(inputStream);
        if (requestLine == null) {
            return null;
        }
        if (requestLine.isBlank()) {
            throw new IllegalArgumentException("요청 라인이 비어 있습니다.");
        }

        final Map<String, String> headers = readHeaders(inputStream);
        final String body = readBody(inputStream, headers);
        return new HttpRequest(RequestLine.from(requestLine), headers, parseParams(body));
    }

    private static String readLine(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream();
        int current = inputStream.read();
        if (current == -1) {
            return null;
        }

        while (current != -1 && current != '\n') {
            if (current != '\r') {
                lineBuffer.write(current);
            }
            current = inputStream.read();
        }
        return lineBuffer.toString(StandardCharsets.UTF_8);
    }

    private static Map<String, String> readHeaders(final InputStream inputStream) throws IOException {
        final Map<String, String> headers = new HashMap<>();

        String line = readLine(inputStream);
        while (line != null && !line.isEmpty()) {
            final int separatorIndex = line.indexOf(HEADER_SEPARATOR);
            if (separatorIndex != -1) {
                final String name = line.substring(0, separatorIndex).trim().toLowerCase();
                final String value = line.substring(separatorIndex + 1).trim();
                headers.put(name, value);
            }
            line = readLine(inputStream);
        }
        return headers;
    }

    private static String readBody(final InputStream inputStream, final Map<String, String> headers)
            throws IOException {
        final String contentLength = headers.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return "";
        }

        final byte[] buffer = inputStream.readNBytes(Integer.parseInt(contentLength));
        return new String(buffer, StandardCharsets.UTF_8);
    }

    private static Map<String, String> parseParams(final String body) {
        final Map<String, String> params = new HashMap<>();
        if (body == null || body.isBlank()) {
            return params;
        }

        for (final String pair : body.split(PARAM_SEPARATOR)) {
            final String[] keyValue = pair.split(KEY_VALUE_SEPARATOR, 2);
            if (keyValue[0].isBlank()) {
                continue;
            }
            final String value = keyValue.length == 2 ? keyValue[1] : "";
            params.put(decode(keyValue[0]), decode(value));
        }
        return params;
    }

    private static String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(final String name) {
        return headers.get(name.toLowerCase());
    }

    public Cookie getCookie() {
        return Cookie.from(headers.get(COOKIE));
    }

    public String getBodyParam(final String name) {
        return bodyParams.get(name);
    }
}
