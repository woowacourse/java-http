package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.common.HttpVersion.HTTP_1_1;
import static org.apache.coyote.http11.response.ResponseBody.EMPTY_RESPONSE_BODY;
import static org.apache.coyote.http11.response.StatusCode.FOUND;

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
        final ResponseBody responseBody = new ResponseBody(content);
        return new Response(responseLine, responseHeader, responseBody);
    }

    public static Response generateRedirectResponse(final String location) {
        final ResponseLine responseLine = new ResponseLine(HTTP_1_1, FOUND);
        final ResponseHeader responseHeader = ResponseHeader.generateRedirectHeader(location);
        return new Response(responseLine, responseHeader, EMPTY_RESPONSE_BODY);
    }

    public String parse() {
        return String.join("\r\n",
                responseLine.parse(),
                responseHeader.parse(),
                "",
                responseBody.getContent());
    }

    public void setCookie(final String cookie) {
        responseHeader.setCookie(cookie);
    }

    public void setHeader(final String key, final String value) {
        responseHeader.setHeader(key, value);
    }
}
