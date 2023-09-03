package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.start.HttpVersion;

public class HttpResponse {
    private final String startLine;
    private final String headers;
    private final String body;

    private HttpResponse(final String startLine, final String headers, final String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(
            final HttpVersion httpVersion,
            final String httpStatus,
            final String contentType,
            final String responseBody
    ) {
        return new HttpResponse(
                httpVersion.getVersion() + " " + httpStatus + " \r\n",
                "Content-Type: " + contentType + " \r\n" +
                        "Content-Length: " + responseBody.getBytes().length + " \r\n",
                responseBody);
    }

    public String getResponse() {
        return startLine + headers + "\r\n" + body;
    }
}
