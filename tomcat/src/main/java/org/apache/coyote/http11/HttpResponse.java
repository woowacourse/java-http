package org.apache.coyote.http11;

public class HttpResponse {

    private HttpResponseStatus responseStatus;
    private HttpResponseHeader responseHeader;
    private String responseBody;
 
    public static HttpResponse of(final HttpResponseStatus responseStatus,
                                  final HttpResponseHeader responseHeader, final String responseBody) {
        return new HttpResponse(responseStatus, responseHeader, responseBody);
    }

    private HttpResponse(final HttpResponseStatus responseStatus,
                         final HttpResponseHeader responseHeader, final String responseBody) {
        this.responseStatus = responseStatus;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                "HTTP/1.1 " + responseStatus.toString() + " ",
                responseHeader.toString() + " ",
                "",
                responseBody);
    }
}
