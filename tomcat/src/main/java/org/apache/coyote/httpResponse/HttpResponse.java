package org.apache.coyote.httpResponse;

public class HttpResponse {

    private final HttpResponseHeader responseHeader;
    private final String body;

    public HttpResponse(
            final String protocol,
            final StatusCode statusCode,
            final String contentType,
            final String body
    ) {
        StatusLine statusLine = new StatusLine(protocol, statusCode);
        this.responseHeader = new HttpResponseHeader(statusLine, contentType);
        this.body = body;
    }

    public String getResponse() {
        String headers = responseHeader.getHeaders();
        return String.join(
                "\r\n",
                headers,
                "",
                body
        );
    }

    public void addHeader(
            final String key,
            final String value
    ) {
        responseHeader.addHeader(key, value);
    }
}
