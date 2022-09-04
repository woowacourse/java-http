package org.apache.coyote.http.response;

import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpVersion;

public class HttpResponse {

    private final HttpVersion version;
    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final int contentLength;
    private final String responseBody;

    public HttpResponse(final HttpVersion version, final HttpStatus httpStatus,
                        final ContentType contentType, final int contentLength, final String responseBody) {
        this.version = version;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.responseBody = responseBody;
    }

    public String convertTemplate() {
        return String.join("\r\n",
                version.getVersion() + " " + httpStatus.getHttpStatusMessage(),
                "Content-Type: " + this.contentType.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + this.responseBody.getBytes().length + " ",
                "",
                this.responseBody);
    }
}
