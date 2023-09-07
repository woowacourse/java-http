package org.apache.coyote.http.response;

import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.common.HttpHeader;
import org.apache.coyote.http.common.HttpHeaders;

import java.util.EnumMap;

public class HttpResponse {

    private static final String SPACE = " ";
    private static final String HEADER_DELIMETER = ": ";
    private static final String CRLF = "\r\n";
    private static final String BLANK_LINE = "";

    private StatusLine statusLine;
    private HttpHeaders httpHeaders;
    private HttpBody body;

    public HttpResponse() {
    }

    HttpResponse(final StatusLine statusLine, final HttpHeaders httpHeaders, final HttpBody body) {
        this.statusLine = statusLine;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public void mapToRedirect(final String redirectUri) {
        changeStatusLine(StatusLine.from(StatusCode.FOUND));
        addHeader(HttpHeader.LOCATION, redirectUri);
        changeBody(HttpBody.empty());
    }

    public void changeStatusLine(final StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void addHeader(final HttpHeader httpHeader, final String value) {
        if (httpHeaders == null) {
            this.httpHeaders = new HttpHeaders(new EnumMap<>(HttpHeader.class));
        }

        this.httpHeaders.add(httpHeader, value);
    }

    public void changeBody(final HttpBody body) {
        this.body = body;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public String serialize() {
        final StringBuilder response = new StringBuilder();

        serializeStatusLine(response);
        serializeHeaders(response);

        if (body == null || body.isEmpty()) {
            return response.toString();
        }

        response.append(HttpHeader.CONTENT_LENGTH.getValue()).append(HEADER_DELIMETER).append(body.getValue().getBytes().length).append(SPACE).append(CRLF);

        serializeBody(response);
        return response.toString();
    }

    private void serializeStatusLine(final StringBuilder response) {
        response.append(statusLine.getProtocol().getName()).append(SPACE);
        response.append(statusLine.getStatusCode().getCode()).append(SPACE);
        response.append(statusLine.getStatusCode().name()).append(SPACE).append(CRLF);
    }

    private void serializeHeaders(final StringBuilder response) {
        httpHeaders.getHeaders().forEach((key, value) -> response.append(key.getValue()).append(HEADER_DELIMETER).append(value).append(SPACE).append(CRLF));
    }

    private void serializeBody(final StringBuilder response) {
        response.append(BLANK_LINE).append(CRLF);
        response.append(body.getValue());
    }
}
