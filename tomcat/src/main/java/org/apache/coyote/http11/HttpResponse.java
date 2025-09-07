package org.apache.coyote.http11;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String RESPONSE_LINE = "%s %s %s" + CRLF;
    private static final String FIELD_CONTENT_FORMAT = "%s: %s"  + CRLF;
    private static final String END_OF_HEADER = CRLF;

    private final HttpProtocol protocol;
    private final HttpStatusCode statusCode;
    private final HttpResponseBody body;

    public HttpResponse(HttpProtocol protocol, HttpStatusCode statusCode, HttpResponseBody body) {
        this.protocol = protocol;
        this.statusCode = statusCode;
        this.body = body;
    }

    public String asString() {
        StringBuilder builder =  new StringBuilder();
        builder.append(String.format(RESPONSE_LINE, protocol.getVersion(), statusCode.getCode(), statusCode.getReasonPhrase()));
        builder.append(String.format(FIELD_CONTENT_FORMAT, "Content-Type", body.getContentType()));
        builder.append(String.format(FIELD_CONTENT_FORMAT, "Content-Length", body.getLength()));
        builder.append(END_OF_HEADER);
        builder.append(body.asString());
        return builder.toString();
    }
}
