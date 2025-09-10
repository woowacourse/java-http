package org.apache.coyote.http11.response;

import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.response.body.ResponseBody;
import org.apache.coyote.http11.response.header.ResponseHeaders;
import org.apache.coyote.http11.response.startline.ResponseLine;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;

    public HttpResponse(final ResponseLine responseLine, final ResponseHeaders responseHeaders,
                        final ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public String toResponseText() {
        return String.join("\r\n",
                responseLine.toResponseText() + " ",
                responseHeaders.toResponseText(),
                responseBody.toResponseText());
    }

    public void setCookie(final HttpCookie cookie) {
        cookie.addToResponseHeaders(responseHeaders);
    }
}
