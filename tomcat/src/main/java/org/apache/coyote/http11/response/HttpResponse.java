package org.apache.coyote.http11.response;

public class HttpResponse {

    private StatusLine statusLine;
    private final ResponseHeader responseHeader;
    private ResponseBody responseBody;

    private HttpResponse(final StatusLine statusLine, final ResponseHeader responseHeader,
                         final ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public static HttpResponse createEmpty() {
        return new HttpResponse(null, ResponseHeader.createEmpty(), null);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }

    public void setStatusLine(final StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void addResponseHeader(final String key, final String value) {
        this.responseHeader.addHeader(key, value);
    }

    public void setResponseBody(final ResponseBody responseBody) {
        this.responseBody = responseBody;
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
                responseBody.getBody());
    }
}
