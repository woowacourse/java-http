package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.ResponseCookie;

public class Http11OutputBuffer {

    private static final String VALID_HTTP_VERSION = "HTTP/1.1";

    private final OutputStream outputStream;

    public Http11OutputBuffer(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(HttpResponse httpResponse) throws IOException {
        outputStream.write(serialize(httpResponse).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    public String serialize(HttpResponse httpResponse) {
        StringBuilder responseBuilder = new StringBuilder();

        StatusLine statusLine = httpResponse.getStatusLine();
        checkHttpVersion(statusLine);
        serializeStatusLine(statusLine, responseBuilder);

        HttpResponseHeader header = httpResponse.getHeader();
        serializeResponseHeader(header, responseBuilder);

        serializeCookie(header, responseBuilder);

        serializeResponseBody(httpResponse, responseBuilder);

        return responseBuilder.toString();
    }

    private void checkHttpVersion(StatusLine statusLine) {
        if (!statusLine.httpVersion().equals(VALID_HTTP_VERSION)) {
            throw new IllegalArgumentException("지원하지 않는 HTTP 버전입니다.");
        }
    }

    private void serializeStatusLine(StatusLine statusLine, StringBuilder responseBuilder) {
        responseBuilder.append(
                String.format("%s %d %s ", statusLine.httpVersion(), statusLine.statusCode().getStatusCode(),
                        statusLine.statusCode().getStatus()));
        responseBuilder.append("\r\n");
    }

    private void serializeResponseHeader(HttpResponseHeader header,
                                         StringBuilder responseBuilder) {
        for (String key : header.getValues().keySet()) {
            responseBuilder.append(key).append(": ").append(header.get(key));
            responseBuilder.append("\r\n");
        }
    }

    private void serializeCookie(HttpResponseHeader header, StringBuilder responseBuilder) {
        if (header.hasCookie()) {
            addCookie(header.getCookie(), responseBuilder);
        }
        responseBuilder.append("\r\n");
    }

    private void addCookie(ResponseCookie responseCookie, StringBuilder responseBuilder) {
        for (String key : responseCookie.getCookieValues().keySet()) {
            responseBuilder.append("Set-Cookie: ").append(key).append("=")
                    .append(responseCookie.getCookieValues().get(key));
        }
    }

    private static void serializeResponseBody(HttpResponse httpResponse, StringBuilder responseBuilder) {
        if (httpResponse.hasResponseBody()) {
            responseBuilder.append(httpResponse.getResponseBody());
        }
    }
}
