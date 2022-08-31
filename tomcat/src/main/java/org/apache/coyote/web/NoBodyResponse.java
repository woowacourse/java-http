package org.apache.coyote.web;

import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;

public class NoBodyResponse extends Response {

    public NoBodyResponse(final HttpStatus httpStatus, final HttpHeaders headers) {
        super(httpStatus, headers);
    }

    public NoBodyResponse(final String version, final HttpStatus httpStatus,
                          final HttpHeaders headers) {
        super(version, httpStatus, headers);
    }

    @Override
    public String createHttpResponse() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getRequestLine());
        getHttpHeaders().getHeaders()
                .forEach((key, value) -> stringBuilder.append(String.format(HEADER_TEMPLATE, key, value)));
        return stringBuilder.toString();
    }
}
