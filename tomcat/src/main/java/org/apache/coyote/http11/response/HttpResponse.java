package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HttpResponse {

    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final String LOCATION_HEADER = "Location";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private final HttpResponseLine httpResponseLine;
    private final Map<String, String> headers;
    private final byte[] body;

    private HttpResponse(
            HttpResponseLine httpResponseLine, Map<String, String> headers, byte[] body) {
        this.httpResponseLine = httpResponseLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse ok(String version, String jsessionid, String contentType, byte[] body) {
        HttpResponseLine httpResponseLine = HttpResponseLine.of(version, 200, "OK");
        Map<String, String> headers = new LinkedHashMap<>();
        addHeader(headers, SET_COOKIE_HEADER, jsessionid);
        addHeader(headers, LOCATION_HEADER, "");
        addHeader(headers, CONTENT_TYPE_HEADER, contentType);
        return new HttpResponse(httpResponseLine, headers, body);
    }

    public static HttpResponse sendRedirect(String version, String jsessionid, String location) {
        HttpResponseLine httpResponseLine = HttpResponseLine.of(version, 302, "Found");
        Map<String, String> headers = new LinkedHashMap<>();
        addHeader(headers, SET_COOKIE_HEADER, jsessionid);
        addHeader(headers, LOCATION_HEADER, location);
        addHeader(headers, CONTENT_TYPE_HEADER, "");
        return new HttpResponse(httpResponseLine, headers, new byte[0]);
    }

    public static HttpResponse notFound(String version, String jsessionid, String contentType, byte[] body) {
        HttpResponseLine httpResponseLine = HttpResponseLine.of(version, 404, "Not Found");
        Map<String, String> headers = new LinkedHashMap<>();
        addHeader(headers, SET_COOKIE_HEADER, jsessionid);
        addHeader(headers, LOCATION_HEADER, "");
        addHeader(headers, CONTENT_TYPE_HEADER, contentType);
        return new HttpResponse(httpResponseLine, headers, body);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        response.append(httpResponseLine.serialize()).append(" \r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            response.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(" \r\n");

        }
        response.append("Content-Length: ").append(body.length).append(" \r\n")
                .append("\r\n");

        outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
    }

    private static void addHeader(Map<String, String> headers, String key, String value) {
        if (value.isEmpty()) {
            return;
        }
        if (key.equals(SET_COOKIE_HEADER)) {
            value = "JSESSIONID=" + value;
        }
        if (key.equals(CONTENT_TYPE_HEADER)) {
            value += ";charset=utf-8";
        }
        headers.put(key, value);
    }
}
