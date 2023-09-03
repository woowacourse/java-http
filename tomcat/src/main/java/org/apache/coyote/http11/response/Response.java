package org.apache.coyote.http11.response;

import org.apache.coyote.http11.header.EntityHeader;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.ResponseHeader;

public class Response {

    public static final Response NOT_FOUND_RESPONSE;
    public static final Response UNAUTHORIZED_RESPONSE;

    static {
        final Headers notFoundHeaders = new Headers();
        notFoundHeaders.addHeader(ResponseHeader.LOCATION, "/404.html");
        NOT_FOUND_RESPONSE = new Response(new StatusLine(StatusCode.FOUND), notFoundHeaders, "");

        final Headers unauthorizedHeaders = new Headers();
        unauthorizedHeaders.addHeader(ResponseHeader.LOCATION, "/401.html");
        UNAUTHORIZED_RESPONSE = new Response(new StatusLine(StatusCode.FOUND), unauthorizedHeaders, "");
    }

    private final StatusLine statusLine;
    private final Headers headers;
    private final String body;

    public Response(final String body) {
        this(StatusLine.DEFAULT_STATUS_LINE, new Headers(), body);
    }

    public Response(final StatusLine statusLine,
                    final String body) {
        this(statusLine, new Headers(), body);
    }

    public Response(final StatusLine statusLine,
                    final Headers headers,
                    final String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public void decideContentType(final String requestAcceptHeader,
                                  final String requestUri) {
        headers.addHeader(EntityHeader.CONTENT_TYPE, decideResponseContentType(requestAcceptHeader, requestUri));
    }

    private String decideResponseContentType(final String requestAcceptHeader,
                                             final String requestUri) {
        String responseFileExtension = requestUri.substring(requestUri.indexOf(".") + 1);
        if ("text/css".equals(requestAcceptHeader) || "css".equals(responseFileExtension)) {
            return  "text/css,*/*;q=0.1";
        }
        if ("application/javascript".equals(requestAcceptHeader) || "js".equals(responseFileExtension)) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    public void decideContentLength() {
        final byte[] bytes = body.getBytes();
        headers.addHeader(EntityHeader.CONTENT_LENGTH, String.valueOf(bytes.length));
    }

    public String parseString() {
        return String.join("\r\n",
                statusLine.parseResponse(),
                headers.parseResponse(),
                "",
                body);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Response{" +
                "statusLine=" + statusLine +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
