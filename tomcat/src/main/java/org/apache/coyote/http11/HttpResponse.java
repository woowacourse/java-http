package org.apache.coyote.http11;

import java.util.Objects;

public class HttpResponse {

    private static final String STATUS_LINE = "HTTP/1.1 200 OK ";
    private static final String CONTENT_TYPE = "Content-Type: text/%s;charset=utf-8 ";
    private static final String CONTENT_LENGTH = "Content-Length: %d ";

    private final String fileExtension;
    private final String responseBody;

    public HttpResponse(String fileExtension, String responseBody) {
        this.fileExtension = fileExtension;
        this.responseBody = responseBody;
    }

    public String extractResponse() {
        return new StringBuilder()
                .append(STATUS_LINE).append("\r\n")
                .append(convertContentType()).append("\r\n")
                .append(convertContentLength()).append("\r\n\r\n")
                .append(responseBody)
                .toString();
    }

    private String convertContentType() {
        return String.format(CONTENT_TYPE, Objects.requireNonNullElse(fileExtension, "html"));
    }

    private String convertContentLength() {
        return String.format(CONTENT_LENGTH, responseBody.getBytes().length);
    }
}
