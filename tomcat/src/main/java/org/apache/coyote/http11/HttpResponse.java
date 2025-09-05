package org.apache.coyote.http11;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String RESPONSE_LINE = "HTTP/1.1 %s " + CRLF;
    private static final String FIELD_CONTENT_FORMAT = "%s: %s "  + CRLF;
    private static final String END_OF_HEADER = CRLF;

    private final StringBuilder builder;

    public HttpResponse(String httpStatusCode, String contentType, String body) {
        this.builder = new StringBuilder();
        init(httpStatusCode, contentType, body);
    }

    private void init(String httpStatusCode, String contentType, String body) {
        builder.append(String.format(RESPONSE_LINE, httpStatusCode));
        builder.append(String.format(FIELD_CONTENT_FORMAT, "Content-Type", contentType + ";charset=utf-8"));
        builder.append(String.format(FIELD_CONTENT_FORMAT, "Content-Length", body.getBytes().length));
        builder.append(END_OF_HEADER);
        builder.append(body);
    }

    public byte[] getBytes() {
        return builder.toString().getBytes();
    }
}
