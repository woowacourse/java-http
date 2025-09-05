package com.techcourse.http.response;

import com.techcourse.http.common.ContentType;
import com.techcourse.http.common.HttpStatus;

public class HttpResponse {

    private final String httpVersion;
    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final String responseBody;

    public HttpResponse(final String httpVersion,
                        final HttpStatus httpStatus,
                        final ContentType contentType,
                        final String responseBody
    ) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public byte[] toBytes() {
        return toStringResponse().getBytes();
    }

    private String toStringResponse() {
        return String.join("\r\n",
                "HTTP/" + httpVersion + " " + httpStatus.getStatusLine() + " ",
                "Content-Type: " + contentType.getMediaType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
