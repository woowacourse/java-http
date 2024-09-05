package org.apache.coyote.http11;

public class HttpResponse {

    private final StatusLine statusLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpResponse(StatusLine statusLine, HttpHeaders headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public String buildHttpResponse() {
        String statusLineResponse = this.statusLine.buildStatusLineResponse();
        String headersResponse = this.headers.buildHttpHeadersResponse();

        return String.join("\r\n",
                statusLineResponse,
                headersResponse,
                "",
                this.body
        );
    }
}
