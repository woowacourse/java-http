package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpVersion;

public class Response {
    private final ResponseLine responseLine;
    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;

    private Response(final ResponseLine responseLine,
                    final ResponseHeader responseHeader,
                    final ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public static Response of(final HttpVersion httpVersion,
                              final StatusCode statusCode,
                              final ContentType contentType,
                              final String content) {
        final ResponseLine responseLine = new ResponseLine(httpVersion, statusCode);
        final ResponseHeader responseHeader = ResponseHeader.of(contentType, content.getBytes().length);
        final ResponseBody responseBody = new ResponseBody(contentType, content);
        return new Response(responseLine, responseHeader, responseBody);
    }

    public String parse() {
        return String.join("\r\n",
                responseLine.parse(),
                responseHeader.parse(),
                "",
                responseBody.getContent());
    }
}
