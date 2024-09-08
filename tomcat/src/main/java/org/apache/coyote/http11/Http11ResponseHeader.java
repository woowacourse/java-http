package org.apache.coyote.http11;

import java.net.http.HttpHeaders;

public class Http11ResponseHeader {

    private static final String RESPONSE_HEADER_FORMAT = String.join("\r\n",
            "%s ",
            "Content-Type: %s;charset=utf-8 ",
            "Content-Length: %s ") + "\r\n" +
            "%s"; // Location과 Set-Cookie를 포함할 부분 추가
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final StatusLine statusLine;
    private final HttpHeaders httpHeaders;

    private Http11ResponseHeader(StatusLine statusLine, HttpHeaders httpHeaders) {
        this.statusLine = statusLine;
        this.httpHeaders = httpHeaders;
    }

    public static Http11ResponseHeader of(StatusLine statusLine, HttpHeaders httpHeaders) {
        return new Http11ResponseHeader(statusLine, httpHeaders);
    }

    public String getResponseHeader() {
        StringBuilder additionalHeaders = new StringBuilder();

        if (httpHeaders.firstValue("Location").isPresent()) {
            additionalHeaders.append("Location: ").append(httpHeaders.firstValue("Location").get()).append("\r\n");
        }

        if (httpHeaders.firstValue("Set-Cookie").isPresent()) {
            additionalHeaders.append("Set-Cookie: ").append(httpHeaders.firstValue("Set-Cookie").get()).append("\r\n");
        }

        return String.format(RESPONSE_HEADER_FORMAT,
                statusLine,
                httpHeaders.firstValue(CONTENT_TYPE).orElse(""),
                httpHeaders.firstValue(CONTENT_LENGTH).orElse(""),
                additionalHeaders);
    }
}

