package org.apache.coyote.http11.response;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeader responseHeader;
    private final String responseBody;

    private HttpResponse(final StatusLine statusLine, final ResponseHeader responseHeader, final String responseBody) {
        this.statusLine = statusLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public static HttpResponse createGetResponse(final StatusLine statusLine, final ResponseHeader responseHeader,
                                                 final String responseBody) {
        return new HttpResponse(statusLine, responseHeader, responseBody);
    }

    public static HttpResponse createPostResponse(final StatusLine statusLine, final ResponseHeader responseHeader) {
        return new HttpResponse(statusLine, responseHeader, null);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public String toString() {
        if (responseBody == null) {
            return String.join("\r\n",
                    statusLine.toString(),
                    responseHeader.toString());
        }
        return String.join("\r\n",
                statusLine.toString(),
                responseHeader.toString(),
                "",
                responseBody);
    }
}
