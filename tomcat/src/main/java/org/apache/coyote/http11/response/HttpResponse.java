package org.apache.coyote.http11.response;

import java.util.List;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.response.body.ResponseBody;
import org.apache.coyote.http11.response.header.ResponseHeader;
import org.apache.coyote.http11.response.header.ResponseHeaders;
import org.apache.coyote.http11.response.startline.ResponseLine;

public class HttpResponse {

    private ResponseLine responseLine;
    private ResponseHeaders responseHeaders;
    private ResponseBody responseBody;

    public static HttpResponse createEmptyResponse() {
        return new HttpResponse(null, ResponseHeaders.createEmptyHeaders(), null);
    }

    public String toResponseText() {
        return String.join("\r\n",
                responseLine.toResponseText() + " ",
                responseHeaders.toResponseText(),
                responseBody.toResponseText());
    }

    public HttpResponse setCookie(final HttpCookie cookie) {
        cookie.addToResponseHeaders(responseHeaders);
        return this;
    }

    public HttpResponse setResponseLine(final ResponseLine responseLine) {
        this.responseLine = responseLine;
        return this;
    }

    public HttpResponse setResponseBody(final ResponseBody responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public HttpResponse addResponseHeader(final ResponseHeader header) {
        this.responseHeaders.add(header);
        return this;
    }

    public HttpResponse addResponseHeaders(final List<ResponseHeader> headers) {
        headers.forEach(this::addResponseHeader);
        return this;
    }

    private HttpResponse(final ResponseLine responseLine, final ResponseHeaders responseHeaders,
                         final ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }
}
