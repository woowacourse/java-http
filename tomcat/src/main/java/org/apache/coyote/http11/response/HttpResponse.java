package org.apache.coyote.http11.response;

public class HttpResponse {
    public static final String DEFAULT_PROTOCOL_VERSION = "HTTP/1.1";
    private final String protocolVersion;

    private final HttpResponseStatus responseStatus;
    private final HttpResponseHeader responseHeader;
    private final String responseBody;

    public static HttpResponse of(final HttpResponseStatus responseStatus,
                                  final HttpResponseHeader responseHeader, final String responseBody) {
        return new HttpResponse(DEFAULT_PROTOCOL_VERSION, responseStatus, responseHeader, responseBody);
    }

    private HttpResponse(final String protocolVersion, final HttpResponseStatus responseStatus,
                         final HttpResponseHeader responseHeader, final String responseBody) {
        this.protocolVersion = protocolVersion;
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
