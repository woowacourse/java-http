package org.apache.coyote.http11.response;

public class HttpResponse {
    public static final String DEFAULT_PROTOCOL_VERSION = "HTTP/1.1";
    private String protocolVersion;
    private HttpResponseStatus responseStatus;
    private HttpResponseHeader responseHeader;
    private String responseBody;

    public void updateResponse(final HttpResponseStatus responseStatus, final HttpResponseHeader responseHeader, final String responseBody) {
        this.protocolVersion = DEFAULT_PROTOCOL_VERSION;
        this.responseStatus = responseStatus;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                protocolVersion + " " + responseStatus.toString() + " ",
                responseHeader.toString() + " ",
                "",
                responseBody);
    }
}
