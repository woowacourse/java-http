package org.apache.coyote.common;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpResponse {

    private static final String SPACE = " ";
    private static final String ENTER = "\r\n";
    private static final String HEADER_SPLIT_DELIMITER = ":";

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final Headers httpHeaders;
    private final ResponseBody responseBody;

    public HttpResponse(final HttpVersion httpVersion, final HttpStatus httpStatus, final Headers httpHeaders, final ResponseBody responseBody) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
        this.responseBody = responseBody;
    }

    public byte[] bytes() {
        final StringBuilder responseForm = new StringBuilder();

        responseForm.append(httpVersion.version())
                .append(SPACE)
                .append(httpStatus.statusCode())
                .append(SPACE)
                .append(httpStatus.statusName())
                .append(SPACE)
                .append(ENTER);

        for (String headerName : httpHeaders.headerNames()) {
            responseForm.append(headerName)
                    .append(HEADER_SPLIT_DELIMITER)
                    .append(SPACE)
                    .append(httpHeaders.getHeaderValue(headerName))
                    .append(SPACE)
                    .append(ENTER);
        }

        responseForm.append(ENTER)
                .append(responseBody.source());

        return responseForm.toString().getBytes(UTF_8);
    }

    public HttpVersion httpVersion() {
        return httpVersion;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public Headers httpHeaders() {
        return httpHeaders;
    }

    public ResponseBody responseBody() {
        return responseBody;
    }
}
