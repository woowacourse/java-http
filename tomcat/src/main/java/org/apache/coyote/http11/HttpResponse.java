package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HttpResponse {

    private final String protocol;
    private int statusCode;
    private String statusMessage;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public HttpResponse() {
        this("HTTP/1.1", 200, "OK");
    }

    public HttpResponse(final int statusCode) {
        this("HTTP/1.1", statusCode, defaultStatusMessage(statusCode));
    }

    public HttpResponse(final int statusCode, final String statusMessage) {
        this("HTTP/1.1", statusCode, statusMessage);
    }

    public HttpResponse(final String protocol, final int statusCode, final String statusMessage) {
        this.protocol = protocol;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public void setHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void setStatus(final int statusCode) {
        this.statusCode = statusCode;
        this.statusMessage = defaultStatusMessage(statusCode);
    }

    public void setStatus(final int statusCode, final String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public void setBody(final String body) {
        this.body = body.getBytes(StandardCharsets.UTF_8);
    }

    public void setBody(final byte[] body) {
        this.body = body.clone();
    }

    public String getProtocol() {
        return protocol;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getStatusLine() {
        return String.join(" ", protocol, String.valueOf(statusCode), statusMessage);
    }

    public Map<String, String> getHeaders() {
        return Map.copyOf(headers);
    }

    public byte[] getBody() {
        return body.clone();
    }

    public byte[] toByteArray() {
        final StringBuilder response = new StringBuilder()
                .append(protocol).append(' ')
                .append(statusCode).append(' ')
                .append(statusMessage).append("\r\n");
        headers.forEach((name, value) -> response.append(name).append(": ").append(value).append("\r\n"));
        if (headers.keySet().stream().noneMatch(name -> name.equalsIgnoreCase("Content-Length"))) {
            response.append("Content-Length: ").append(body.length).append("\r\n");
        }
        response.append("\r\n");

        final byte[] header = response.toString().getBytes(StandardCharsets.UTF_8);
        final byte[] result = new byte[header.length + body.length];
        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(body, 0, result, header.length, body.length);
        return result;
    }

    private static String defaultStatusMessage(final int statusCode) {
        return switch (statusCode) {
            case 200 -> "OK";
            case 302 -> "Found";
            case 401 -> "Unauthorized";
            case 404 -> "Not Found";
            default -> "";
        };
    }
}
