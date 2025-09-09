package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String RESPONSE_LINE = "%s %s %s" + CRLF;
    private static final String END_OF_HEADER = CRLF;

    private final HttpProtocol protocol;
    private final HttpStatusCode statusCode;
    private final HttpResponseHeader responseHeader;
    private final HttpResponseBody body;

    public HttpResponse(HttpProtocol protocol, HttpStatusCode statusCode, HttpResponseBody body) {
        this.protocol = protocol;
        this.statusCode = statusCode;
        this.responseHeader = setHeader(body);
        this.body = body;
    }

    private HttpResponseHeader setHeader(HttpResponseBody body) {
        HttpResponseHeader responseHeader = new HttpResponseHeader();
        responseHeader.addHeader("Content-Type", body.getContentType());
        responseHeader.addHeader("Content-Length", String.valueOf(body.getLength()));
        return  responseHeader;
    }

    public String asString() {
        StringBuilder builder =  new StringBuilder();
        builder.append(String.format(RESPONSE_LINE, protocol.getVersion(), statusCode.getCode(), statusCode.getReasonPhrase()));
        builder.append(responseHeader.asString());
        builder.append(END_OF_HEADER);
        builder.append(body.asString());
        return builder.toString();
    }

    public void setLocation(String location) {
        responseHeader.addHeader("Location", location);
    }

    public HttpResponseBody getBody() {
        return body;
    }
}
