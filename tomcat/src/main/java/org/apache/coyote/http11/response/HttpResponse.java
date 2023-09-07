package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;

import java.util.List;

public class HttpResponse {

    private final HttpStatus httpStatus;

    private final List<String> headers;

    private final String body;

    public HttpResponse(final HttpStatus httpStatus, final List<String> headers, final String body) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public String toString() {
        String responseLine = String.join(" ", "HTTP/1.1", String.valueOf(httpStatus.getCode()), httpStatus.getMessage());
        String responseHeader = convertHeaderToString();

        if (body == null) {
            return String.join("\r\n", responseLine,
                    responseHeader);
        }
        return String.join("\r\n", responseLine,
                responseHeader,
                body);
    }

    private String convertHeaderToString() {
        StringBuilder sb = new StringBuilder();
        for (String header : headers) {
            sb.append(header + "\r\n");
        }
        return sb.toString();
    }
}
