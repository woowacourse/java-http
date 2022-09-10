package org.apache.coyote.http11.response;

import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.HttpVersion;

public class HttpResponse {

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final Location location;
    private final ContentType contentType;
    private final String responseBody;

    public HttpResponse(final HttpVersion httpVersion, final HttpStatus httpStatus, final Location location,
                        final ContentType contentType, final String responseBody) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.location = location;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public byte[] getResponseAsBytes() {
        return createResponse().getBytes();
    }

    private String createResponse() {
        return String.join("\r\n",
                getStartLine(),
                getContentType(),
                getContentLength(),
                getLocation(),
                responseBody);
    }

    private String getStartLine() {
        return String.format("%s %s ", httpVersion.getValue(), httpStatus.getValue());
    }

    private String getContentType() {
        return String.format("Content-Type: %s", contentType.getValue());
    }

    private String getContentLength() {
        return String.format("Content-Length: %s ", responseBody.getBytes().length);
    }

    private String getLocation() {
        if (location.isEmpty()) {
            return "";
        }
        return String.format("Location: %s", location.getValue());
    }
}
