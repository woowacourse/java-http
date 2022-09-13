package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.authorization.HttpCookie;
import org.apache.coyote.http11.exception.RequestHeaderException;

public class HttpRequest {
    private final HttpRequestStartLine startLine;
    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    private HttpRequest(HttpRequestStartLine startLine, HttpRequestHeader header,
                        HttpRequestBody body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        final String startLine = reader.readLine();
        if (startLine == null) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        final HttpRequestHeader httpRequestHeader = HttpRequestHeader.from(parseHeader(reader));

        final String bodyLine = getRequestBody(reader,
                (String) httpRequestHeader.getHeader("Content-Length"));

        return new HttpRequest(HttpRequestStartLine.from(startLine),
                httpRequestHeader,
                new HttpRequestBody(getRequestBodyParams(bodyLine)));
    }

    private static Map<String, Object> parseHeader(BufferedReader reader) throws IOException {
        String line;
        final Map<String, Object> headers = new HashMap<>();

        while (!"".equals(line = reader.readLine()) && line != null) {
            final String[] header = line.split(": ");

            checkExistValue(header);

            headers.put(header[0], header[1]);
        }
        return headers;
    }

    private static void checkExistValue(final String[] header) {
        if ("".equals(header[1].trim())) {
            throw new RequestHeaderException();
        }
    }

    private static String getRequestBody(final BufferedReader reader, final String contentLength) throws IOException {
        if (contentLength == null) {
            return null;
        }
        final int contentSize = Integer.parseInt(contentLength);
        char[] buffer = new char[contentSize];
        reader.read(buffer, 0, contentSize);
        return new String(buffer);
    }

    private static Map<String, String> getRequestBodyParams(final String bodyLine) {
        final Map<String, String> body = new HashMap<>();

        if (bodyLine == null || "".equals(bodyLine.trim())) {
            return body;
        }

        final String[] params = bodyLine.split("&");
        for (String param : params) {
            final String[] keyValue = param.split("=");
            body.put(keyValue[0], keyValue[1]);
        }
        return body;
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public String getUri() {
        return startLine.getUri();
    }

    public String getBodyValue(final String key) {
        return body.getInfo(key);
    }

    public String getCookieValue(final String key) {
        final HttpCookie cookie = (HttpCookie) header.getHeader("Cookie");
        return cookie.getCookieValue(key);
    }
}
