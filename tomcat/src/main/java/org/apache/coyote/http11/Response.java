package org.apache.coyote.http11;

public class Response {

    private static final String SPACE = " ";
    private static final String LINE_SEPARATOR = "\r\n";

    private String sourceCode;
    private HttpStatusCode httpStatusCode;
    private final HttpHeaders headers = new HttpHeaders();

    public Response() {
    }

    public Response(final HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public void setHttpStatusCode(final HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public void setSourceCode(final String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public void putHeader(final String name, final String value) {
        headers.set(name, value);
    }

    public void putHeader(final String name, final Integer value) {
        headers.set(name, String.valueOf(value));
    }

    public String toHttpResponse() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HTTP/1.1 ").append(httpStatusCode.toHttpResponse()).append(SPACE).append(LINE_SEPARATOR);

        for (final var header : headers.getHeaders().entrySet()) {
            stringBuilder.append(header.getKey()).append(": ").append(header.getValue())
                    .append(SPACE).append(LINE_SEPARATOR);
        }

        stringBuilder.append(LINE_SEPARATOR);
        if (sourceCode != null) {
            stringBuilder.append(sourceCode);
        }

        return stringBuilder.toString();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }
}
