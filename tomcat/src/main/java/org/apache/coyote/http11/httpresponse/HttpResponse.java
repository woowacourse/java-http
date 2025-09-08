package org.apache.coyote.http11.httpresponse;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;

    public HttpResponse(final ResponseLine responseLine, final ResponseHeaders responseHeaders,
                        final ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public String toResponseText() {
        return String.join("\r\n",
                responseLine.toResponseText() + " ",
                responseHeaders.toResponseText(),
                responseBody.toResponseText());
    }

    public void addHeader(final String key, final String value) {
        responseHeaders.add(key, value);
    }
}
