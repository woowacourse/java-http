package org.apache.coyote.http11.httpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private final StatusLine statusLine;
    private ResponseHeader responseHeader;
    private ResponseBody responseBody;

    private HttpResponse(
            final StatusLine statusLine,
            final ResponseHeader responseHeader,
            final ResponseBody responseBody
    ) {
        this.statusLine = statusLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public static HttpResponse status(final HttpStatus httpStatus) {
        final StatusLine statusLine = StatusLine.build(httpStatus);
        final ResponseBody responseBody = ResponseBody.empty();
        final ResponseHeader responseHeader = ResponseHeader.defaultOf(responseBody.getBody());

        return new HttpResponse(statusLine, responseHeader, responseBody);
    }

    public HttpResponse body(final String body) throws IOException {
        this.responseHeader = responseHeader.build(body);
        this.responseBody = responseBody.build(body);

        return this;
    }

    public HttpResponse build(final String path, final String body) {
        final String contentType = ContentType.getContentType(path);

        this.responseHeader = responseHeader.build(body, contentType);
        this.responseBody = responseBody.build(body);

        return this;
    }

    public byte[] getBytes() throws IOException {
        final String header = buildHeader();
        final String body = responseBody.getBody();

        return (header + body).getBytes(StandardCharsets.UTF_8);
    }

    private String buildHeader() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(statusLine.toString()).append(CRLF);

        final Map<String, String> headers = responseHeader.getHeaders();
        for (String headersName : headers.keySet()) {
            stringBuilder.append(headersName).append(": ").append(headers.get(headersName)).append(CRLF);
        }
        stringBuilder.append(CRLF);

        return stringBuilder.toString();
    }
}
