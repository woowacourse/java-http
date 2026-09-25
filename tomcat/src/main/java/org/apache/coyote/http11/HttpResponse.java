package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpResponse {

    private static final String DEFAULT_PROTOCOL_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";

    private final OutputStream outputStream;
    private final Headers headers;

    private String protocolVersion;
    private int statusCode;
    private String reasonPhrase;
    private byte[] body;
    private boolean appendTrailingSpaces;

    public HttpResponse() {
        this(null);
    }

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
        this.protocolVersion = DEFAULT_PROTOCOL_VERSION;
        this.statusCode = 200;
        this.reasonPhrase = "OK";
        this.headers = new Headers();
        this.body = new byte[0];
        this.appendTrailingSpaces = false;
    }

    public void setProtocolVersion(final String protocolVersion) {
        if (protocolVersion == null || protocolVersion.isBlank()
                || protocolVersion.contains(" ")) {
            throw new IllegalArgumentException("HTTP 버전이 올바르지 않습니다.");
        }
        this.protocolVersion = protocolVersion;
    }

    public void setStatusCode(final int statusCode) {
        setStatus(statusCode, defaultReasonPhrase(statusCode));
    }

    public void setStatus(final int statusCode, final String reasonPhrase) {
        if (statusCode < 100 || statusCode > 599) {
            throw new IllegalArgumentException("상태 코드는 100에서 599 사이여야 합니다.");
        }
        if (reasonPhrase == null || reasonPhrase.contains("\r") || reasonPhrase.contains("\n")) {
            throw new IllegalArgumentException("Reason-Phrase가 올바르지 않습니다.");
        }
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getStatusLine() {
        return protocolVersion + " " + statusCode + " " + reasonPhrase;
    }

    public void setHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void addHeader(final String name, final String value) {
        setHeader(name, value);
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setBody(final String body) {
        setBody(body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8));
    }

    public void setBody(final byte[] body) {
        this.body = body == null ? new byte[0] : body.clone();
    }

    public String getBody() {
        return new String(body, StandardCharsets.UTF_8);
    }

    public byte[] getBodyBytes() {
        return body.clone();
    }

    void setAppendTrailingSpaces(final boolean appendTrailingSpaces) {
        this.appendTrailingSpaces = appendTrailingSpaces;
    }

    public byte[] toByteArray() {
        final StringBuilder response = new StringBuilder();
        final String trailingSpace = appendTrailingSpaces ? " " : "";
        response.append(getStatusLine()).append(trailingSpace).append(CRLF);

        final Map<String, String> responseHeaders = headers.asMap();
        responseHeaders.forEach((name, value) ->
                response.append(name).append(": ").append(value)
                        .append(trailingSpace).append(CRLF)
        );

        if (!headers.contains("Content-Length")) {
            response.append("Content-Length: ").append(body.length)
                    .append(trailingSpace).append(CRLF);
        }
        response.append(CRLF);

        final byte[] headerBytes = response.toString().getBytes(StandardCharsets.UTF_8);
        final byte[] result = new byte[headerBytes.length + body.length];
        System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
        System.arraycopy(body, 0, result, headerBytes.length, body.length);
        return result;
    }

    public void send() throws IOException {
        if (outputStream == null) {
            throw new IllegalStateException("응답을 보낼 OutputStream이 없습니다.");
        }
        send(outputStream);
    }

    public void send(final OutputStream outputStream) throws IOException {
        outputStream.write(toByteArray());
        outputStream.flush();
    }

    @Override
    public String toString() {
        return new String(toByteArray(), StandardCharsets.UTF_8);
    }

    private String defaultReasonPhrase(final int statusCode) {
        return switch (statusCode) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 204 -> "No Content";
            case 302 -> "Found";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "";
        };
    }
}
