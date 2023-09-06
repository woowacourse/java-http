package org.apache.coyote.http.response;

import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.common.HttpHeader;
import org.apache.coyote.http.common.HttpHeaders;

import java.util.Map;

public class HttpResponse {

    public static final String SPACE = " ";
    public static final String HEADER_DELIMETER = ": ";
    public static final String CRLF = "\r\n";
    public static final String BLANK_LINE = "";

    private final StatusLine statusLine;
    private final HttpHeaders httpHeaders;
    private final HttpBody body;

    HttpResponse(final StatusLine statusLine, final HttpHeaders httpHeaders, final HttpBody body) {
        this.statusLine = statusLine;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public static HttpResponse redirect(final String redirectUri) {
        return HttpResponse.builder()
                .statusLine(StatusLine.from(StatusCode.FOUND))
                .httpHeaders(new HttpHeaders(Map.of(HttpHeader.LOCATION, redirectUri)))
                .body(HttpBody.empty())
                .build();
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

        response.append(HttpHeader.CONTENT_LENGTH)
                .append(HEADER_DELIMETER)
                .append(body.getValue().getBytes().length)
                .append(SPACE).append(CRLF);

        serializeBody(response);
        return response.toString();
    }

    private void serializeStatusLine(final StringBuilder response) {
        response.append(statusLine.getProtocol().getName()).append(SPACE);
        response.append(statusLine.getStatusCode().getCode()).append(SPACE);
        response.append(statusLine.getStatusCode().name()).append(SPACE).append(CRLF);
    }

    private void serializeHeaders(final StringBuilder response) {
        httpHeaders.getHeaders().forEach((key, value) -> response.append(key.getValue())
                .append(HEADER_DELIMETER)
                .append(value)
                .append(CRLF)
        );
    }

    private void serializeBody(final StringBuilder response) {
        response.append(BLANK_LINE).append(CRLF);
        response.append(body.getValue()).append(SPACE).append(CRLF);
    }
}
