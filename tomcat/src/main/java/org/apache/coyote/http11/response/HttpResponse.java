package org.apache.coyote.http11.response;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private final ResponseLine responseLine;
    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;

    public HttpResponse(
            ResponseLine responseLine,
            ResponseHeaders responseHeaders,
            ResponseBody responseBody
    ) {
        this.responseLine = responseLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public String extractResponse() {
        return new StringBuilder()
                .append(responseLine.convertStatusLine()).append(CRLF)
                .append(responseHeaders.convertResponseHeaders()).append(CRLF)
                .append(responseBody.getBody())
                .toString();
    }

}
