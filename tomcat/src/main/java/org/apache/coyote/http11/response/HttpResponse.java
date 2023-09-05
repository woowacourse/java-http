package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.MimeType;
import org.apache.coyote.http11.common.HttpStatus;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final MimeType mimeType;
    private final String responseBody;

    public HttpResponse(HttpStatus httpStatus, MimeType mimeType, String responseBody) {
        this.httpStatus = httpStatus;
        this.mimeType = mimeType;
        this.responseBody = responseBody;
    }

    public String extractResponse() {
        return new StringBuilder()
                .append(convertStatusLine()).append("\r\n")
                .append(convertContentType()).append("\r\n")
                .append(convertContentLength()).append("\r\n")
                .append("\r\n")
                .append(responseBody)
                .toString();
    }

    private String convertStatusLine() {
        String statusLineFormat = "HTTP/1.1 %s %s ";

        return String.format(statusLineFormat, httpStatus.getStatusCode(), httpStatus.name());
    }

    private String convertContentType() {
        String contentTypeFormat = "Content-Type: %s;charset=utf-8 ";

        return String.format(contentTypeFormat, mimeType.getContentType());
    }

    private String convertContentLength() {
        String contentLengthFormat = "Content-Length: %d ";

        return String.format(contentLengthFormat, responseBody.getBytes().length);
    }
}
