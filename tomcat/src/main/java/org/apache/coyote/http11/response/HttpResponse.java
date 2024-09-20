package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.coyote.http11.constants.HttpHeader;
import org.apache.coyote.http11.constants.HttpStatus;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String BLANK = " ";

    private final String version;
    private HttpStatus httpStatus;
    private Map<HttpHeader, String> headers;
    private String body;

    public HttpResponse(final String version) {
        this.version = version;
        headers = new LinkedHashMap<>();
    }

    public void addHeader(final HttpHeader key, final String value) {
        headers.put(key, value);
    }

    public byte[] serializeResponse() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(version).append(BLANK).append(httpStatus.getHttpStatusMessage()).append(BLANK)
                .append(CRLF);
        headers.forEach(
                (key, value) -> stringBuilder.append(key.getHeaderName()).append(": ").append(value).append(BLANK)
                        .append(CRLF));
        stringBuilder.append(CRLF);
        stringBuilder.append(body);

        return stringBuilder.toString().getBytes();
    }

    public void setHttpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setBody(final String body) {
        headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        this.body = body;
    }
}
