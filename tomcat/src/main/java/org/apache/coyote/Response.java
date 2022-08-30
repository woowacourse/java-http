package org.apache.coyote;

import org.apache.coyote.support.ContentType;
import org.apache.coyote.support.HttpStatus;

public class Response {

    private final String version;
    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final String responseBody;

    public Response(final String version,
                    final HttpStatus httpStatus,
                    final ContentType contentType,
                    final String responseBody) {
        this.version = version;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public String createHttpResponse() {
        return String.join("\r\n",
                String.format("%s %d %s ", version, httpStatus.getStatusCode(), httpStatus.getMessage()),
                String.format("Content-Type: %s ", contentType.getValue()),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
