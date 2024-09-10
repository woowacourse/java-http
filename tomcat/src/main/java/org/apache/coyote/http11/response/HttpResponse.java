package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpHeaderKey;
import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private final HttpStatus status;
    private final String protocolVersion;
    private final ResponseHeaders header;
    private final ResponseBody body;

    public HttpResponse(HttpStatus status, String protocolVersion, ResponseHeaders header, ResponseBody body) {
        this.status = status;
        this.protocolVersion = protocolVersion;
        this.header = header;
        this.body = body;
    }

    public HttpResponse(HttpStatus status, String protocolVersion) {
        this(status, protocolVersion, new ResponseHeaders(), null);
    }

    public HttpResponse(HttpStatus status, String protocolVersion, ResponseBody body) {
        this(status, protocolVersion, new ResponseHeaders(), body);
        header.add(HttpHeaderKey.CONTENT_TYPE, body.getContentType() + ";charset=utf-8");
        header.add(HttpHeaderKey.CONTENT_LENGTH, body.getContentLength());
    }

    public void setRedirect(final String location) {
        header.add(HttpHeaderKey.LOCATION, location);
        header.add(HttpHeaderKey.CONTENT_LENGTH, "0");
        header.add(HttpHeaderKey.CONNECTION, "close");
    }

    public void setCookie(final String name, final String value) {
        header.add(HttpHeaderKey.SET_COOKIE, name + "=" + value);
    }

    public String getResponse() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(makeStatusLine()).append("\r\n");
        responseBuilder.append(header.getHeaderResponse()).append("\r\n");
        responseBuilder.append("\r\n");

        if (body != null) {
            responseBuilder.append(body.getValue());
        }
        return responseBuilder.toString();
    }

    private String makeStatusLine() {
        return String.format("%s %s %s ", protocolVersion, status.getCode(), status.getReasonPhrase());
    }
}
