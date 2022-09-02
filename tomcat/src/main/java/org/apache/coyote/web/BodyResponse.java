package org.apache.coyote.web;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.support.HttpHeader;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;

public class BodyResponse extends Response {

    private final String responseBody;

    public BodyResponse(final HttpStatus httpStatus, final HttpHeaders httpHeaders, final String responseBody) {
        super(httpStatus, httpHeaders);
        this.responseBody = responseBody;
        httpHeaders.put(HttpHeader.CONTENT_LENGTH.getValue(),
                String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
    }

    public BodyResponse(final String version, final HttpStatus httpStatus, final HttpHeaders httpHeaders,
                        final String responseBody) {
        super(version, httpStatus, httpHeaders);
        this.responseBody = responseBody;
        httpHeaders.put(HttpHeader.CONTENT_LENGTH.getValue(),
                String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
    }

    @Override
    public String createHttpResponse() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getRequestLine());
        getHttpHeaders().getHeaders()
                .forEach((key, value) -> stringBuilder.append(String.format(HEADER_TEMPLATE, key, value)));
        stringBuilder.append(String.format(HEADER_TEMPLATE, HttpHeader.SET_COOKIE.getValue(), getCookieTemplate()));
        stringBuilder.append("\r\n")
                .append(responseBody);
        return stringBuilder.toString();
    }
}
