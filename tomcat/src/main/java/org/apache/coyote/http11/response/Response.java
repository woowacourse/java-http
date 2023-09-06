package org.apache.coyote.http11.response;

import org.apache.coyote.http11.header.EntityHeader;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.RequestHeader;
import org.apache.coyote.http11.header.ResponseHeader;
import org.apache.coyote.http11.request.Request;

import static org.apache.coyote.http11.header.EntityHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.header.ResponseHeader.LOCATION;
import static org.apache.coyote.http11.response.StatusCode.FOUND;

public class Response {

    private final StatusLine statusLine;
    private final Headers headers;
    private final String body;

    public Response(final String body) {
        this(StatusLine.DEFAULT_STATUS_LINE, new Headers(), body);
    }

    public Response(final StatusLine statusLine,
                    final Headers headers,
                    final String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static Response getRedirectResponse(final String path) {
        final Headers redirectHeaders = new Headers();
        redirectHeaders.addHeader(LOCATION, path);
        return new Response(new StatusLine(FOUND), redirectHeaders, "");
    }

    public static Response getNotFoundResponse() {
        final Headers notFoundHeaders = new Headers();
        notFoundHeaders.addHeader(LOCATION, "/404.html");
        return new Response(new StatusLine(FOUND), notFoundHeaders, "");
    }

    public static Response getUnauthorizedResponse() {
        final Headers unauthorizedHeaders = new Headers();
        unauthorizedHeaders.addHeader(LOCATION, "/401.html");
        return new Response(new StatusLine(FOUND), unauthorizedHeaders, "");
    }

    public void decideContentType(final Request request) {
        final String acceptHeaderValue = request.getHeaders().getValue(RequestHeader.ACCEPT);
        final String requestPath = request.getRequestLine().getRequestPath();
        final String contentTypeValue = decideResponseContentType(acceptHeaderValue, requestPath);
        headers.addHeader(CONTENT_TYPE, contentTypeValue);
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
