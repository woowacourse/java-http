package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.HttpHeaderKey;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.cookie.Cookie;

public class HttpResponse {

    private final OutputStream outputStream;
    private final StatusLine statusLine;
    private final ResponseHeaders headers;
    private final ResponseBody body;

    private HttpResponse(
            OutputStream outputStream,
            HttpStatus status,
            String protocolVersion,
            ResponseHeaders headers,
            ResponseBody body
    ) {
        this.outputStream = outputStream;
        this.statusLine = new StatusLine(protocolVersion, status);
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse(OutputStream outputStream, HttpStatus status, String protocolVersion) {
        this(outputStream, status, protocolVersion, new ResponseHeaders(), null);
    }

    public HttpResponse(OutputStream outputStream, HttpStatus status, String protocolVersion, ResponseBody body) {
        this(outputStream, status, protocolVersion, new ResponseHeaders(), body);
        headers.add(HttpHeaderKey.CONTENT_TYPE, body.getContentType() + ";charset=utf-8");
        headers.add(HttpHeaderKey.CONTENT_LENGTH, body.getContentLength());
    }

    public void setCookie(Cookie cookie) {
        headers.add(HttpHeaderKey.SET_COOKIE, cookie.getCookieString());
    }

    public void sendRedirect(String location) throws IOException {
        headers.add(HttpHeaderKey.LOCATION, location);
        outputStream.write(getResponse().getBytes());
        outputStream.flush();
    }

    public void sendResponse() throws IOException {
        outputStream.write(getResponse().getBytes());
        outputStream.flush();
    }

    private String getResponse() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(statusLine.formatStatusLine()).append("\r\n");
        responseBuilder.append(headers.getHeaderResponse()).append("\r\n");
        responseBuilder.append("\r\n");

        if (body != null) {
            responseBuilder.append(body.getValue());
        }
        return responseBuilder.toString();
    }
}
