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

    private HttpResponseLine httpResponseLine;
    private final Map<String, String> headers;
    private byte[] body;

    private HttpResponse(
            HttpResponseLine httpResponseLine, Map<String, String> headers, byte[] body) {
        this.httpResponseLine = httpResponseLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse empty() {
        return new HttpResponse(null, new LinkedHashMap<>(), null);
    }

    public void writeOk(String version, String jsessionid, String contentType, byte[] body) {
        setHttpResponseLine(HttpResponseLine.of(version, 200, "OK"));
        headers.clear();
        addHeader(SET_COOKIE_HEADER, jsessionid);
        addHeader(LOCATION_HEADER, "");
        addHeader(CONTENT_TYPE_HEADER, contentType);
        setBody(body);
    }

    public void sendRedirect(String version, String jsessionid, String location) {
        setHttpResponseLine(HttpResponseLine.of(version, 302, "Found"));
        headers.clear();
        addHeader(SET_COOKIE_HEADER, jsessionid);
        addHeader(LOCATION_HEADER, location);
        addHeader(CONTENT_TYPE_HEADER, "");
        setBody(new byte[0]);
    }

    public void writeNotFound(String version, String jsessionid, String contentType, byte[] body) {
        setHttpResponseLine(HttpResponseLine.of(version, 404, "Not Found"));
        headers.clear();
        addHeader(SET_COOKIE_HEADER, jsessionid);
        addHeader(LOCATION_HEADER, "");
        addHeader(CONTENT_TYPE_HEADER, contentType);
        setBody(body);
    }

    private void setHttpResponseLine(HttpResponseLine httpResponseLine) {
        this.httpResponseLine = httpResponseLine;
    }

    private void addHeader(String key, String value) {
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

    private void setBody(byte[] body) {
        this.body = body;
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
}
