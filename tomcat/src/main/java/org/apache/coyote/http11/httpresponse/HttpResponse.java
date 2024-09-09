package org.apache.coyote.http11.httpresponse;

public class HttpResponse {

    private final HttpStatusLine httpStatusLine;
    private final HttpResponseHeader httpResponseHeader;
    private final HttpResponseBody httpResponseBody;

    public HttpResponse(
            HttpStatusLine httpStatusLine,
            HttpResponseHeader httpResponseHeader,
            HttpResponseBody httpResponseBody
    ) {
        this.httpStatusLine = httpStatusLine;
        this.httpResponseHeader = httpResponseHeader;
        this.httpResponseBody = httpResponseBody;
    }

    public HttpResponse(HttpStatusLine httpStatusLine, HttpResponseHeader httpResponseHeader) {
        this(httpStatusLine, httpResponseHeader, null);
    }

    public byte[] getBytes() {
        String statusLine = httpStatusLine.getString();

        String responseHeader = httpResponseHeader.getString();

        if (httpResponseBody != null) {
            String responseBody = httpResponseBody.getBody();
            String join = String.join("\r\n",
                    statusLine,
                    responseHeader,
                    responseBody);
            return join.getBytes();
        }
        String join = String.join("\r\n",
                statusLine,
                responseHeader);
        return join.getBytes();
    }

    public HttpStatusLine getHttpStatusLine() {
        return httpStatusLine;
    }

    public HttpResponseHeader getHttpResponseHeader() {
        return httpResponseHeader;
    }

    public HttpResponseBody getHttpResponseBody() {
        return httpResponseBody;
    }
}
